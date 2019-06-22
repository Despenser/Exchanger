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
import ru.golubyatnikov.money.exchange.model.util.*;
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

    private ProjectInformant informant;
    private CurrencyService currencyService;
    private List<Currency> allCurrency;
    private Validator validator;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(CurrencyRateController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

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

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showCurrencyData(newValue));

        Platform.runLater(this::setValidationOnPane);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    private void showCurrencyData(Currency currency){
        if (currency != null){
            btnSetRate.setDisable(false);
            txtFieldRateCBRF.setText(String.valueOf(currency.getValue()));
            txtFieldRateBuy.setText(String.valueOf(currency.getValueBuy()));
            txtFieldRateSale.setText(String.valueOf(currency.getValueSale()));
            comboBoxCurrency.getSelectionModel().select(IsoCode.valueOf(currency.getCharCode()));
            dateCurrency.setValue(currency.getCurrencyDate());
        } else {
            clearFields(txtFieldRateCBRF, txtFieldRateBuy, txtFieldRateSale);
            btnSetRate.setDisable(true);
        }
    }

    private void clearFields(TextField... fields){
        informant.logInfo("Очистка предзаполненых полей на форме");
        for (TextField field: fields) field.clear();
    }

    private void setValidationOnPane(){
        informant.logInfo("Установка валидации полей");
        validator.onlyFloat(7,5, txtFieldRateCBRF, txtFieldRateSale, txtFieldRateBuy);
    }

    @FXML
    private void setRate() {
        informant.logInfo("Процесс сохранения заданных курсов валют");

        Currency currency = tableView.getSelectionModel().getSelectedItem();
        currency.setValueBuy(Float.parseFloat(txtFieldRateBuy.getText()));
        currency.setValueSale(Float.parseFloat(txtFieldRateSale.getText()));
        currencyService.update(currency);

        informant.logInfoAndShowNotificationComplete(resources.getString("data_about_currency") + " " + currency.getName() + " " +
                resources.getString("on") + " " + DateEditor.formatLocalDateToString(dateCurrency.getValue(), DatePattern.PATTERN_DOT) + " " +
                resources.getString("successfully_update") + "\n\n" +
                resources.getString("rate_sale") + ": " + currency.getValueSale() + "\n" +
                resources.getString("rate_buy") + ": " + currency.getValueBuy() + "\n");
    }

    @FXML
    private void showAll() {
        informant.logInfo("Отображение всех курсов валют");
        List<Currency> allCurrencies = currencyService.findAll();
        populateTableView(allCurrencies);
        comboBoxCurrency.getSelectionModel().clearSelection();
        dateCurrency.setValue(null);
    }

    @FXML
    private void searchByFilter() {
        if (dateCurrency.getValue() != null && comboBoxCurrency.getSelectionModel().getSelectedItem() != null) {
            List<Currency> currencyList = currenciesFilter(comboBoxCurrency.getSelectionModel().getSelectedItem(), dateCurrency.getValue());
            if (currencyList.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("no_courses_found_on_date")));
            populateTableView(currencyList);
        }
    }

    private List<Currency> currenciesFilter(IsoCode code, LocalDate date){
        return allCurrency.stream()
                .filter(s -> s.getCharCode().equals(code.name()) && s.getCurrencyDate().equals(date))
                .collect(Collectors.toList());
    }

    private void populateTableView(List<Currency> currencyList){
        informant.logInfo("Наполнение таблицы данными");
        tableView.setItems(FXCollections.observableArrayList(currencyList));
    }
}