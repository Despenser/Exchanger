package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.golubyatnikov.money.exchange.model.entity.Currency;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import ru.golubyatnikov.money.exchange.model.service.CurrencyService;
import ru.golubyatnikov.money.exchange.model.util.ConfigComboBox;
import ru.golubyatnikov.money.exchange.model.util.DateEditor;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.Validator;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class CurrencyRateController implements Initializable {

    @FXML private Button btnSetRate;
    @FXML private ComboBox<IsoCode> comboBoxCurrency;
    @FXML private DatePicker dateCurrency;
    @FXML private TextField txtFieldRateCBRF, txtFieldRateBuy, txtFieldRateSale;
    @FXML private TableView<Currency> tableView;
    @FXML private TableColumn<Currency, LocalDate> colDate;
    @FXML private TableColumn<Currency, String> colCharCode, colName;
    @FXML private TableColumn<Currency, Integer> colNumCode, colNominal;
    @FXML private TableColumn<Currency, Float> colValue, colValueBuy, colValueSale;

    private CurrencyService currencyService;
    private List<Currency> allCurrency;
    private Validator validator;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        currencyService = new CurrencyService();
        validator = Validator.getInstance();
        allCurrency = FXCollections.observableArrayList(currencyService.findAll());

        ConfigComboBox.configComboBox(comboBoxCurrency);
        comboBoxCurrency.getItems().remove(IsoCode.RUB);
        comboBoxCurrency.getSelectionModel().clearSelection();

        btnSetRate.setDisable(true);

        tableView.setPlaceholder(new Label(resources.getString("select_currency_and_date_to_get_rates_for_given_day")));

        colDate.setCellValueFactory(cellData -> cellData.getValue().currencyDateProperty());
        colCharCode.setCellValueFactory(cellData -> cellData.getValue().charCodeProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colNumCode.setCellValueFactory(cellData -> cellData.getValue().numCodeProperty().asObject());
        colNominal.setCellValueFactory(cellData -> cellData.getValue().nominalProperty().asObject());
        colValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        colValueBuy.setCellValueFactory(cellData -> cellData.getValue().valueBuyProperty().asObject());
        colValueSale.setCellValueFactory(cellData -> cellData.getValue().valueSaleProperty().asObject());

        comboBoxCurrency.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (dateCurrency.getValue() != null && newValue != null) {
                List<Currency> currencyList = currenciesFilter(newValue, dateCurrency.getValue());
                if (currencyList.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("no_courses_found_on_date")));
                else populateTableView(currencyList);
            }
        });

        dateCurrency.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && comboBoxCurrency.getValue() != null){
                List<Currency> currencyList = currenciesFilter(comboBoxCurrency.getValue(), newValue);
                if (currencyList.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("no_courses_found_on_date")));
                populateTableView(currencyList);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> show(newValue));

        Platform.runLater(this::setValidationOnPane);
    }

    @FXML
    private void setRate() {
        Currency currency = tableView.getSelectionModel().getSelectedItem();
        currency.setValueBuy(Float.parseFloat(txtFieldRateBuy.getText()));
        currency.setValueSale(Float.parseFloat(txtFieldRateSale.getText()));
        currencyService.update(currency);
        Notification.getInstance().complete(resources.getString("data_about_currency") + " " + currency.getName() + " " +
                resources.getString("on") + " " + DateEditor.formatLocalDateToString(dateCurrency.getValue(), DatePattern.PATTERN_DOT) + " " +
                resources.getString("successfully_update") + "\n\n" +
                resources.getString("rate_sale") + ": " + currency.getValueSale() + "\n" +
                resources.getString("rate_buy") + ": " + currency.getValueBuy() + "\n");
    }

    private List<Currency> currenciesFilter(IsoCode code, LocalDate date){
        return allCurrency.stream()
                .filter(s -> s.getCharCode().equals(code.name()) && s.getCurrencyDate().equals(date))
                .collect(Collectors.toList());
    }

    private void show(Currency currency){
        if (currency != null){
            btnSetRate.setDisable(false);
            txtFieldRateCBRF.setText(String.valueOf(currency.getValue()));
            txtFieldRateBuy.setText(String.valueOf(currency.getValueBuy()));
            txtFieldRateSale.setText(String.valueOf(currency.getValueSale()));
        } else {
            clearFields(txtFieldRateCBRF, txtFieldRateBuy, txtFieldRateSale);
            btnSetRate.setDisable(true);
        }
    }

    private void populateTableView(List<Currency> currencyList){
        tableView.setItems(FXCollections.observableArrayList(currencyList));
    }

    private void clearFields(TextField... fields){
        for (TextField field: fields) field.clear();
    }

    private void setValidationOnPane(){
        validator.onlyFloat(7,5, txtFieldRateCBRF, txtFieldRateSale, txtFieldRateBuy);
    }
}