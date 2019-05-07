package ru.golubyatnikov.money.exchange.controller.calculation;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.service.CurrencyService;
import ru.golubyatnikov.money.exchange.model.util.*;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import ru.golubyatnikov.money.exchange.model.util.Exchanger;
import ru.golubyatnikov.money.exchange.model.entity.Currency;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


public class CalcController implements Initializable {

    @FXML private ComboBox<IsoCode> comboBoxCurrencyIn, comboBoxCurrencyOut;
    @FXML private ImageView imgBuyUSD, imgBuyEUR, imgBuyGBP, imgBuyCNY, imgSaleUSD, imgSaleEUR, imgSaleGBP, imgSaleCNY;
    @FXML private Label labelResult, labelBuyUSD, labelBuyEUR, labelBuyGBP, labelBuyCNY, labelSaleUSD, labelSaleEUR, labelSaleGBP, labelSaleCNY;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private TextField txtField;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
    private static final String IMG_RATE_UP = "view/style/img/currency/rateUp.png";
    private static final String IMG_RATE_DOWN = "view/style/img/currency/rateDown.png";

    private IsoCode IN, OUT;
    private Exchanger exchanger;
    private Notification notification;
    private ResourceBundle resources;
    private List<Currency> currenciesForTwoWeeks, actualCurrencies, lastButOneCurrencies;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        exchanger = Exchanger.getInstance();
        notification = Notification.getInstance();
        CurrencyService currencyService = new CurrencyService();
        LocalDate now = LocalDate.now();

        Validator.getInstance().onlyFloat(7, 2, txtField);

        actualCurrencies = FXCollections.observableArrayList(currencyService.findLastDate());
        lastButOneCurrencies = FXCollections.observableArrayList(currencyService.findLastButOneDate());
        currenciesForTwoWeeks = FXCollections.observableArrayList(currencyService.currenciesForPeriod(now.minusDays(20), now));
        currenciesForTwoWeeks.sort(Comparator.comparing(Currency::getCurrencyDate));

        ConfigComboBox.configComboBox(comboBoxCurrencyIn, comboBoxCurrencyOut);

        comboBoxCurrencyIn.valueProperty().addListener((observable, oldValue, newValue) -> {
            ConfigComboBox.ifSameCurrency(comboBoxCurrencyIn,comboBoxCurrencyOut,oldValue,newValue);
            calculate();
        });

        comboBoxCurrencyOut.valueProperty().addListener((observable, oldValue, newValue) -> {
            ConfigComboBox.ifSameCurrency(comboBoxCurrencyOut, comboBoxCurrencyIn,oldValue,newValue);
            calculate();
        });

        txtField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (txtField.getText().isEmpty()) labelResult.setText("");
            calculate();
        });

        Platform.runLater(this::rate);
        Platform.runLater(this::populateChart);
        Platform.runLater(this::calculate);
    }

    private boolean rateSaleIsNotNull(Currency currency){
        return currency.getValueSale() != 0.00;
    }

    private boolean rateBuyIsNotNull(Currency currency){
        return currency.getValueBuy() != 0.00;
    }

    private float saleDifference(Currency actual, Currency lastButOne){
        return actual.getValueSale() - lastButOne.getValueSale();
    }

    private float buyDifference(Currency actual, Currency lastButOne){
        return actual.getValueBuy() - lastButOne.getValueBuy();
    }

    private void setImgRate(float difference, ImageView imageView){
        if (difference < 0) {
            imageView.setImage(new Image(IMG_RATE_DOWN));
            Tooltip.install(imageView, notification.tooltip(resources.getString("rate_down") + " " + DECIMAL_FORMAT.format(difference)));
        } else {
            imageView.setImage(new Image(IMG_RATE_UP));
            Tooltip.install(imageView, notification.tooltip(resources.getString("rate_up") + " " + DECIMAL_FORMAT.format(difference)));
        }
    }

    private void setLabelAndImg(Currency lastButOne, Currency actual, Label labelBuy, Label labelSale, ImageView imgBuy, ImageView imgSale){
        if (rateSaleIsNotNull(lastButOne) && rateBuyIsNotNull(lastButOne)){
            labelBuy.setText(String.valueOf(actual.getValueBuy()));
            labelSale.setText(String.valueOf(actual.getValueSale()));
            setImgRate(buyDifference(actual, lastButOne), imgBuy);
            setImgRate(saleDifference(actual, lastButOne), imgSale);
        }
    }

    private void calculate() {
        IN = comboBoxCurrencyIn.getSelectionModel().getSelectedItem();
        OUT = comboBoxCurrencyOut.getSelectionModel().getSelectedItem();

        Currency sale = actualCurrencies.stream().filter(s -> s.getCharCode().equals(IN.name())).findAny().orElse(null);
        Currency buy = actualCurrencies.stream().filter(s -> s.getCharCode().equals(OUT.name())).findAny().orElse(null);

        if (!txtField.getText().isEmpty()) {

            if (IN != IsoCode.RUB && OUT == IsoCode.RUB && sale != null) {
                if (rateSaleIsNotNull(sale)) {
                    labelResult.setText(DECIMAL_FORMAT.format(exchanger.buy(sale, Float.parseFloat(txtField.getText()))) + " " + resources.getString("rub"));
                } else labelResult.setText(resources.getString("buy_rate_not_set"));
            }
            if (IN == IsoCode.RUB && OUT != IsoCode.RUB && buy != null) {
                if (rateBuyIsNotNull(buy)) {
                    labelResult.setText(DECIMAL_FORMAT.format(exchanger.sale(buy, Float.parseFloat(txtField.getText()))) + " " + buy.getCharCode());
                } else labelResult.setText(resources.getString("sale_rate_not_set"));
            }
            if (IN != IsoCode.RUB && OUT != IsoCode.RUB && !IN.equals(OUT) && sale != null && buy != null) {
                if (rateBuyIsNotNull(buy) || rateSaleIsNotNull(sale)) {
                    labelResult.setText(DECIMAL_FORMAT.format(exchanger.cross(sale, buy, Float.parseFloat(txtField.getText()))) + " " + buy.getCharCode());
                } else labelResult.setText(resources.getString("buy_or_sale_rate_not_set"));
            }
        }
    }

    private void rate(){
        Currency USD = actualCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.USD.name())).findAny().orElse(null);
        Currency EUR = actualCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.EUR.name())).findAny().orElse(null);
        Currency GBP = actualCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.GBP.name())).findAny().orElse(null);
        Currency CNY = actualCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.CNY.name())).findAny().orElse(null);

        Currency lastButOneUSD = lastButOneCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.USD.name())).findAny().orElse(null);
        Currency lastButOneEUR = lastButOneCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.EUR.name())).findAny().orElse(null);
        Currency lastButOneGBP = lastButOneCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.GBP.name())).findAny().orElse(null);
        Currency lastButOneCNY = lastButOneCurrencies.stream().filter(s -> s.getCharCode().equals(IsoCode.CNY.name())).findAny().orElse(null);

        if (USD != null && EUR != null && GBP != null && CNY != null && lastButOneUSD != null && lastButOneEUR != null && lastButOneGBP != null && lastButOneCNY != null) {

            setLabelAndImg(lastButOneUSD, USD, labelBuyUSD, labelSaleUSD, imgBuyUSD, imgSaleUSD);
            setLabelAndImg(lastButOneEUR, EUR, labelBuyEUR, labelSaleEUR, imgBuyEUR, imgSaleEUR);
            setLabelAndImg(lastButOneGBP, GBP, labelBuyGBP, labelSaleGBP, imgBuyGBP, imgSaleGBP);
            setLabelAndImg(lastButOneCNY, CNY, labelBuyCNY, labelSaleCNY, imgBuyCNY, imgSaleCNY);
        }
    }

    private void populateChart() {

        XYChart.Series<String, Number> usd = new XYChart.Series<>();
        XYChart.Series<String, Number> eur = new XYChart.Series<>();
        XYChart.Series<String, Number> cny = new XYChart.Series<>();
        XYChart.Series<String, Number> gbp = new XYChart.Series<>();

        usd.setName(resources.getString("usd"));
        eur.setName(resources.getString("eur"));
        cny.setName(resources.getString("cny"));
        gbp.setName(resources.getString("gbp"));

        for (Currency currency : currenciesForTwoWeeks) {

            if (currency.getCharCode().equals(IsoCode.USD.name()))
                usd.getData().add(new XYChart.Data<>(DateEditor.formatLocalDateToString(currency.getCurrencyDate(),
                        DatePattern.PATTERN_DOT), (currency.getValue()/currency.getNominal())));

            else if (currency.getCharCode().equals(IsoCode.EUR.name()))
                eur.getData().add(new XYChart.Data<>(DateEditor.formatLocalDateToString(currency.getCurrencyDate(),
                        DatePattern.PATTERN_DOT), (currency.getValue()/currency.getNominal())));

            else if (currency.getCharCode().equals(IsoCode.CNY.name()))
                cny.getData().add(new XYChart.Data<>(DateEditor.formatLocalDateToString(currency.getCurrencyDate(),
                        DatePattern.PATTERN_DOT), (currency.getValue()/currency.getNominal())));

            else if (currency.getCharCode().equals(IsoCode.GBP.name()))
                gbp.getData().add(new XYChart.Data<>(DateEditor.formatLocalDateToString(currency.getCurrencyDate(),
                        DatePattern.PATTERN_DOT), (currency.getValue()/currency.getNominal())));
        }

        lineChart.getData().add(usd);
        lineChart.getData().add(eur);
        lineChart.getData().add(cny);
        lineChart.getData().add(gbp);

        for (XYChart.Series<String, Number> s : lineChart.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install(d.getNode(), Notification.getInstance().tooltip(resources.getString("currency_date") + " " +
                        d.getXValue() + "\n" + resources.getString("currency_rate") + " " + d.getYValue()));

                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
    }
}