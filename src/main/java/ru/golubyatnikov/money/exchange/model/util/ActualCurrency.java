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
import java.util.*;
import java.util.stream.Collectors;


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
        if (isEmptyCurrencyBase()) uploadForMonth();
        else uploadForToday();
    }

    private boolean isEmptyCurrencyBase() {
        return currencyService.isEmptyTable().equals(new BigInteger("0"));
    }

    private LocalDate getLocalDateNow() {
        return LocalDate.now();
    }

    public boolean uploadForMonth() throws IOException, JDOMException {
        List<List<Currency>> listCurrenciesForMonth = new ArrayList<>();
        for (int i = 30; i >= 0; i--) {
            List<Currency> list = loadCurrenciesFromSite(getLocalDateNow().minusDays(i));
            listCurrenciesForMonth.add(list);
        }
        return checkAndUploadForMonth(listCurrenciesForMonth);
    }

    private boolean checkAndUploadForMonth(List<List<Currency>> currenciesForMonth) {
        if (currenciesForMonth.isEmpty()) return false;

        List<Currency> currenciesInBase = currencyService.findAll();
        if (currenciesInBase.isEmpty()) {
            currenciesForMonth.forEach(listCurrency -> listCurrency.forEach(currency -> {
                List<Currency> test = currencyService.findByDateAndValueAndCharCode(currency);
                if (test.isEmpty()) currencyService.create(currency);
            }));
            return true;
        }
        else {
            for (Currency currencyInBase : currenciesInBase) {
                for (List<Currency> currencyList : currenciesForMonth) {
                    currencyList.removeIf(currency -> isSameCurrencies(currency, currencyInBase));
                }
            }
            currenciesForMonth.removeIf(list -> list.size() == 0);

            if (!currenciesForMonth.isEmpty()) {
                currenciesForMonth.forEach(listCurrencies -> listCurrencies.forEach(currency -> {
                    currencyService.create(currency);
                }));
                return true;
            }
            else return false;
        }
    }

    public boolean uploadForToday() throws IOException, JDOMException {
        List<Currency> actualCurrencies = loadCurrenciesFromSite(getLocalDateNow());
        return checkAndUploadForToday(actualCurrencies);
    }

    private boolean checkAndUploadForToday(List<Currency> actualCurrencies) {
        if (actualCurrencies.isEmpty()) return false;

        List<Currency> currenciesInBase = currencyService.findAll();
        if (currenciesInBase.isEmpty()) {
            actualCurrencies.forEach(currency -> currencyService.create(currency));
            return true;
        }
        else {
            List<Currency> lastCurrencies = currencyService.findLastDate();

            Currency actual = actualCurrencies.get(0);
            Currency lastInBase = lastCurrencies.get(0);

            boolean result = isSameCurrencies(actual, lastInBase);

            if (!result) {
                actualCurrencies.forEach(currency -> currencyService.create(currency));
                return true;
            }
            else return false;
        }
    }

    private boolean isSameCurrencies(Currency actual, Currency lastInBase){
        LocalDate actualDate = actual.getCurrencyDate();
        float actualValue = actual.getValue();
        String actualCharCode = actual.getCharCode();

        LocalDate lastDateInBase = lastInBase.getCurrencyDate();
        float lastValueInBase = lastInBase.getValue();
        String lastCharCode = lastInBase.getCharCode();

        return actualDate.equals(lastDateInBase) && actualValue == lastValueInBase && actualCharCode.equals(lastCharCode);
    }

    private List<Currency> loadCurrenciesFromSite(LocalDate date) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        List<Currency> currencies = new ArrayList<>();
        Document document = saxBuilder.build(new URL(CBRF + DateEditor.formatLocalDateToString(date, DatePattern.PATTERN_SLASH)));
        Element rootNode = document.getRootElement();
        List<Element> list = rootNode.getChildren("Valute");
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
}