package ru.golubyatnikov.money.exchange.model.util;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import ru.golubyatnikov.money.exchange.model.entity.Currency;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import ru.golubyatnikov.money.exchange.model.service.CurrencyService;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ActualCurrency {

    private static volatile ActualCurrency instance;
    private static final String CBRF = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=";
    private static ArrayList<String> ISO_LIST;
    private CurrencyService currencyService;

    private ActualCurrency() {
        ISO_LIST = new ArrayList<>();
        currencyService = new CurrencyService();
        for (IsoCode code : IsoCode.values()) ISO_LIST.add(code.name());
    }

    public static ActualCurrency getInstance() {
        if (instance == null) {
            synchronized (ActualCurrency.class) {
                if (instance == null) instance = new ActualCurrency();
            }
        }
        return instance;
    }

    public void uploadForTodayOrMonth() throws IOException, JDOMException {
        upload();
        uploadForMonth();
    }

    public void upload() throws IOException, JDOMException {
        if (!isEmptyCurrencyBase()){
            List<Currency> actualCurrencies = loadCurrenciesFromSite(getLocalDateNow());
            checkAndUploadCurrencies(actualCurrencies, currencyService.findLastDate());
        }
    }

    //TODO проверять что курсы уже есть в базе (загружает повторно ДУБЛИКАТЫ)
    public void uploadForMonth() throws IOException, JDOMException {
        if (isEmptyCurrencyBase()) {
            List<Currency> listCurrencies = loadCurrenciesFromSite(getLocalDateNow().minusDays(30));
            listCurrencies.forEach(currency -> currencyService.create(currency));
            for (int i = 29; i >= 1; i--) {
                List<Currency> list = loadCurrenciesFromSite(getLocalDateNow().minusDays(i));
                List<Currency> last = currencyService.findLastDate();
                checkAndUploadCurrencies(list, last);
            }
        }
    }

    private boolean isEmptyCurrencyBase() {
        return currencyService.isEmptyTable().equals(new BigInteger("0"));
    }

    private List<Currency> loadCurrenciesFromSite(LocalDate date) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        List<Currency> currencies = new ArrayList<>();
        Document document = saxBuilder.build(new URL(CBRF + DateEditor.formatLocalDateToString(date, DatePattern.PATTERN_SLASH)));
        Element rootNode = document.getRootElement();
        List list = rootNode.getChildren("Valute");
        Currency currency;
        for (Object obj : list) {
            Element node = (Element) obj;
            if (ISO_LIST.contains(node.getChildText("CharCode"))) {
                currency = new Currency();
                currency.setCurrencyDate(DateEditor.parseToLocalDate(rootNode.getAttribute("Date").getValue(), DatePattern.PATTERN_DOT));
                currency.setNumCode(Integer.parseInt(node.getChildText("NumCode")));
                currency.setCharCode(node.getChildText("CharCode"));
                currency.setNominal(Integer.parseInt(node.getChildText("Nominal")));
                currency.setName(node.getChildText("Name"));
                currency.setValue(Float.parseFloat(node.getChildText("Value").replace(',', '.')));
                currencies.add(currency);
            }
        }
        return currencies;
    }

    private LocalDate getLocalDateNow() {
        return LocalDate.now();
    }

    //TODO сделать boolean
    private void checkAndUploadCurrencies(List<Currency> actualCurrencies, List<Currency> lastCurrencies) {
        if (!actualCurrencies.isEmpty()) {
            LocalDate actualDate = actualCurrencies.get(0).getCurrencyDate();
            LocalDate lastDateInBase = lastCurrencies.get(0).getCurrencyDate();
            float actualValue = actualCurrencies.get(0).getValue();
            float lastValue = lastCurrencies.get(0).getValue();

            if (!actualDate.equals(lastDateInBase))
                actualCurrencies.forEach(currency -> currencyService.create(currency));
            else if (actualDate.equals(lastDateInBase) && actualValue != lastValue)
                actualCurrencies.forEach(currency -> currencyService.create(currency));
        }
    }
}