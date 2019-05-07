package ru.golubyatnikov.money.exchange.controller.operation;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.golubyatnikov.money.exchange.controller.AbstractModalController;
import ru.golubyatnikov.money.exchange.controller.client.ClientHandlerController;
import ru.golubyatnikov.money.exchange.controller.client.ClientChoicerController;
import ru.golubyatnikov.money.exchange.model.entity.Currency;
import ru.golubyatnikov.money.exchange.model.enumirate.Mode;
import ru.golubyatnikov.money.exchange.model.service.ClientService;
import ru.golubyatnikov.money.exchange.model.util.*;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import ru.golubyatnikov.money.exchange.model.service.CompanyService;
import ru.golubyatnikov.money.exchange.model.service.CurrencyService;
import ru.golubyatnikov.money.exchange.model.service.OperationService;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public class OperationHandlerController extends AbstractModalController implements Initializable {

    @FXML private VBox vBoxClientButtons;
    @FXML private HBox hBoxClient;
    @FXML private DatePicker datePickerDate;
    @FXML private Label labelTitle, labelWrong;
    @FXML private GridPane gridPaneFirst, gridPaneSecond;
    @FXML private Button btnAction, chooseClient, addClient;
    @FXML private ComboBox<IsoCode> comboBoxCurrencyIn, comboBoxCurrencyOut;
    @FXML private TextArea txtAreaClient, txtAreaEmployee, txtAreaCompany;
    @FXML private TextField txtFieldCode, txtFieldTime, txtFieldType, txtFieldRate, txtFieldSumIn, txtFieldSumOut;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
    private static final Float RATE_IS_NULL = 0.0F;
    private OperationController operationController;
    private OperationService operationService;
    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;
    private Validator validator;
    private Exchanger exchanger;
    private Currency currencyIn, currencyOut;
    private List<Currency> actualCurrencies;
    private TypeOperation type;
    private List<TypeOperation> typeOperation;
    private Operation operation;
    private Client client;
    private Employee employee;
    private Event event;
    private Mode mode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        loaderFXML = LoaderFXML.getInstance();
        validator = Validator.getInstance();
        notification = Notification.getInstance();
        exchanger = Exchanger.getInstance();
        operationService = new OperationService();
        actualCurrencies = FXCollections.observableArrayList(new CurrencyService().findLastDate());
        employee = loaderFXML.getMain().getCurrentEmployee();
        typeOperation = loaderFXML.getMain().getTypeOperations();

        labelWrong.getStyleClass().add("red-text");

        chooseClient.setTooltip(notification.tooltip(resources.getString("choice_client")));
        addClient.setTooltip(notification.tooltip(resources.getString("create_client")));

        datePickerDate.setOnMouseClicked(e -> { if (!datePickerDate.isEditable()) datePickerDate.hide(); });

        ConfigComboBox.configComboBox(comboBoxCurrencyIn, comboBoxCurrencyOut);
        comboBoxCurrencyIn.getSelectionModel().clearSelection();
        comboBoxCurrencyOut.getSelectionModel().clearSelection();

        comboBoxCurrencyIn.valueProperty().addListener((observable, oldValue, newValue) -> {
            currencyIn = actualCurrencies.stream().filter(s -> s.getCharCode().equals(comboBoxCurrencyIn.getSelectionModel().getSelectedItem().name())).findAny().orElse(null);
            ConfigComboBox.ifSameCurrency(comboBoxCurrencyIn, comboBoxCurrencyOut, oldValue, newValue);
            if (!comboBoxCurrencyOut.getSelectionModel().isEmpty())
                rateAndType(txtFieldRate, txtFieldType, comboBoxCurrencyIn, comboBoxCurrencyOut);
            if (!comboBoxCurrencyIn.getSelectionModel().isEmpty() && !comboBoxCurrencyOut.getSelectionModel().isEmpty())
                calculation();
        });

        comboBoxCurrencyOut.valueProperty().addListener((observable, oldValue, newValue) -> {
            currencyOut = actualCurrencies.stream().filter(s -> s.getCharCode().equals(comboBoxCurrencyOut.getSelectionModel().getSelectedItem().name())).findAny().orElse(null);
            txtFieldSumIn.setEditable(true);
            ConfigComboBox.ifSameCurrency(comboBoxCurrencyOut, comboBoxCurrencyIn, oldValue, newValue);
            if (!comboBoxCurrencyIn.getSelectionModel().isEmpty())
                rateAndType(txtFieldRate, txtFieldType, comboBoxCurrencyIn, comboBoxCurrencyOut);
            if (!comboBoxCurrencyIn.getSelectionModel().isEmpty() && !comboBoxCurrencyOut.getSelectionModel().isEmpty())
                calculation();
        });

        txtFieldSumIn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (txtFieldSumIn.getText().isEmpty()) txtFieldSumOut.setText(String.valueOf(RATE_IS_NULL));
            else calculation();
        });

        Platform.runLater(this::checkMode);
        Platform.runLater(() -> { if (mode != Mode.VIEW) setValidationOnPane();});
    }

    void setEvent(Event event) {
        this.event = event;
    }

    void setOperationController(OperationController controller) {
        this.operationController = controller;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public TextArea getTxtAreaClient() {
        return txtAreaClient;
    }

    @Override
    public void checkMode() {
        switch (((Button) event.getSource()).getId()) {
            case "addOperation":
                mode = Mode.CREATE;
                labelTitle.setText(resources.getString("form_create_operation"));
                btnAction.setText(resources.getString("button_create"));
                operation = new Operation();
                operation.setCode(generateUniqueId());
                operation.setDate(LocalDate.now());
                operation.setTime(LocalTime.now().withNano(0));
                operation.setEmployee(employee);
                txtFieldCode.setText(String.valueOf(operation.getCode()));
                datePickerDate.setValue(operation.getDate());
                txtFieldTime.setText(operation.getTime().toString());
                txtAreaEmployee.setText(employee.getSurname() + " " + employee.getName() + " " + employee.getMiddleName());
                txtAreaCompany.setText(getCompanyData());
                setRestrictions();
                break;
            case "editOperation":
                mode = Mode.EDIT;
                labelTitle.setText(resources.getString("form_edit_operation"));
                btnAction.setText(resources.getString("button_edit"));
                operation = operationController.getTableView().getSelectionModel().getSelectedItem();
                client = operation.getClient();
                showDetails();
                setRestrictions();
                break;
            default:
                mode = Mode.VIEW;
                labelTitle.setText(resources.getString("form_view_operation"));
                btnAction.setText(resources.getString("button_view"));
                operation = operationController.getTableView().getSelectionModel().getSelectedItem();
                lockFields(true, gridPaneFirst, gridPaneSecond);
                showDetails();
                break;
        }
    }

    @Override
    public void useButtonAction(ActionEvent event) {
        action(event, mode);
    }

    @Override
    public Operation create(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("is_input_data_correct"))) {
                operation = contain();
                report();
                if (notification.confirmation(resources.getString("are_documents_signed"))) {
                    operationService.create(operation);
                    operationController.getOperations().add(operation);
                    notification.complete(resources.getString("operation") + " " + operation.getCode() + " " +
                            resources.getString("operation_successfully_created"));
                    closeWindow(event);
                    return operation;
                }
            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
        return null;
    }

    @Override
    public void update(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("do_change_operation_data"))) {
                Operation operation = contain();
                operationService.update(operation);
                operationController.getTableView().refresh();
                notification.complete(resources.getString("information_about_operation_successfully_updated"));
                closeWindow(event);
            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
    }

    @Override
    public void report() {
        Platform.runLater(() -> Report.getInstance().operationReport(resources.getString("statement_of_operation") + " " +
                operation.getCode(), operation.getClient(), operation.getEmployee(), operation));
    }

    @Override
    public Operation contain() {
        operation.setCodeIn(comboBoxCurrencyIn.getSelectionModel().getSelectedItem().name());
        operation.setCodeOut(comboBoxCurrencyOut.getSelectionModel().getSelectedItem().name());
        operation.setTypeOperation(type);
        operation.setRate(Float.parseFloat(txtFieldRate.getText()));
        operation.setSumIn(txtFieldSumIn.getText());
        operation.setSumOut(txtFieldSumOut.getText());
        operation.setClient(client);
        loaderFXML.getMain().setClients(FXCollections.observableArrayList(new ClientService().findAll()));

        if (mode == Mode.EDIT) {
            operation.getCurrencies().clear();
            operation.getCurrencies().add(currencyIn);
            operation.getCurrencies().add(currencyOut);
        }

        if (mode == Mode.CREATE) {
            Set<Currency> currencies = new HashSet<>();
            if (currencyIn != null) currencies.add(currencyIn);
            if (currencyOut != null) currencies.add(currencyOut);
            operation.setCurrencies(currencies);
            client.getOperations().add(operation);
        }
        return operation;
    }

    @Override
    public void showDetails() {
        txtFieldCode.setText(String.valueOf(operation.getCode()));
        datePickerDate.setValue(operation.getDate());
        txtFieldTime.setText(operation.getTime().toString());
        txtFieldType.setText(operation.getTypeOperation().getType());
        comboBoxCurrencyIn.setValue(IsoCode.valueOf(operation.getCodeIn()));
        comboBoxCurrencyOut.setValue(IsoCode.valueOf(operation.getCodeOut()));
        txtFieldRate.setText(String.valueOf(operation.getRate()));
        txtFieldSumIn.setText(String.valueOf(operation.getSumIn()));
        txtFieldSumOut.setText(String.valueOf(operation.getSumOut()));
        Client client = operation.getClient();
        txtAreaClient.setText(
                resources.getString("serial_number") + " " + client.getPassport().getSerial() + "/" + client.getPassport().getNumber() + "\n" +
                resources.getString("fio") + " " + client.getSurname() + " " + client.getName() + " " + client.getMiddleName());
        txtAreaEmployee.setText(operation.getEmployee().getSurname() + " " + operation.getEmployee().getName() + " " + operation.getEmployee().getMiddleName());
        txtAreaCompany.setText(getCompanyData());

        if (mode == Mode.VIEW) {
            hBoxClient.getChildren().remove(vBoxClientButtons);
            txtAreaClient.setMinWidth(300);
        }
    }

    @Override
    public void setValidationOnPane() {
        validator.validatePane(btnAction, gridPaneFirst, gridPaneSecond);
        validator.onlyFloat(7, 2, txtFieldSumIn);
    }

    @FXML
    private void chooseClient() {
        ClientChoicerController controller = loaderFXML.loadModalWindow("client/choicerClient",
                resources.getString("form_choice_client"), false);
        controller.setOperationHandlerController(this);
    }

    @FXML
    private void addClient(ActionEvent event) {
        ClientHandlerController controller = loaderFXML.loadModalWindow("client/editorClient",
                resources.getString("form_create_client"), false);
        controller.setController(this);
        controller.setEvent(event);
    }

    private boolean rateSaleIsNotNull(Currency currency) {
        return currency.getValueSale() != RATE_IS_NULL;
    }

    private boolean rateBuyIsNotNull(Currency currency) {
        return currency.getValueBuy() != RATE_IS_NULL;
    }

    private Long generateUniqueId() {
        long val;
        do {
            final UUID uid = UUID.randomUUID();
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uid.getLeastSignificantBits());
            buffer.putLong(uid.getMostSignificantBits());
            final BigInteger bi = new BigInteger(buffer.array());
            val = bi.longValue();
        } while (val < 0);
        return val;
    }

    private void rateAndType(TextField txtFieldRate, TextField txtFieldType, ComboBox<IsoCode> in, ComboBox<IsoCode> out) {
        if (in.getSelectionModel().getSelectedItem().equals(IsoCode.RUB)) {
            txtFieldType.setText(typeOperation.get(1).getType());
            type = typeOperation.get(1);
            txtFieldRate.setText(String.valueOf(currencyOut.getValueSale()));
        } else if (out.getSelectionModel().getSelectedItem().equals(IsoCode.RUB)) {
            txtFieldType.setText(typeOperation.get(0).getType());
            type = typeOperation.get(0);
            txtFieldRate.setText(String.valueOf(currencyIn.getValueBuy()));
        } else if (!in.getSelectionModel().getSelectedItem().equals(IsoCode.RUB) && !out.getSelectionModel().getSelectedItem().equals(IsoCode.RUB)) {
            txtFieldType.setText(typeOperation.get(2).getType());
            type = typeOperation.get(2);
            txtFieldRate.setText(String.valueOf(currencyOut.getValueSale()));
        }
    }

    private void setRestrictions() {
        for (Currency currency : actualCurrencies) {
            if (currency.getValueBuy() == RATE_IS_NULL || currency.getValueSale() == RATE_IS_NULL) {
                lockFields(true, gridPaneFirst, gridPaneSecond);
                addClient.setDisable(true);
                chooseClient.setDisable(true);
                if (mode != Mode.VIEW) labelWrong.setText(resources.getString("buy_or_sale_rate_on_actual_date_not_set"));
                if (mode == Mode.EDIT) btnAction.setDisable(true);
            }
        }
    }

    private void calculation() {
        IsoCode IN = comboBoxCurrencyIn.getSelectionModel().getSelectedItem();
        IsoCode OUT = comboBoxCurrencyOut.getSelectionModel().getSelectedItem();

        if (!txtFieldSumIn.getText().isEmpty()) {
            if (IN != IsoCode.RUB && OUT == IsoCode.RUB && currencyIn != null && rateBuyIsNotNull(currencyIn)) {
                txtFieldSumOut.setText(DECIMAL_FORMAT.format(exchanger.buy(currencyIn, Float.parseFloat(txtFieldSumIn.getText()))));
            }
            if (IN == IsoCode.RUB && OUT != IsoCode.RUB && currencyOut != null && rateSaleIsNotNull(currencyOut)) {
                txtFieldSumOut.setText(DECIMAL_FORMAT.format(exchanger.sale(currencyOut, Float.parseFloat(txtFieldSumIn.getText()))));
            }
            if (IN != IsoCode.RUB && OUT != IsoCode.RUB && !IN.equals(OUT) && currencyOut != null && currencyIn != null
                    && (rateBuyIsNotNull(currencyIn) || rateSaleIsNotNull(currencyOut))) {
                txtFieldSumOut.setText(DECIMAL_FORMAT.format(exchanger.cross(currencyIn, currencyOut, Float.parseFloat(txtFieldSumIn.getText()))));
            }
        }
    }

    private String getCompanyData() {
        try {
            Company company = new CompanyService().findById(1L);
            return  resources.getString("name_company") + " " + company.getName() + "\n" +
                    resources.getString("licence") + " " + company.getLicence() + "\n" +
                    resources.getString("cor_score") + " " + company.getCorScore() + "\n" +
                    resources.getString("bik") + " " + company.getBIK() + "\n" +
                    resources.getString("inn") + " " + company.getINN() + "\n" +
                    resources.getString("kpp") + " " + company.getKPP() + "\n" +
                    resources.getString("ogrn") + " " + company.getOGRN() + "\n" +
                    resources.getString("legal_address") + " " + company.getLegalAddress() + "\n" +
                    resources.getString("phone") + " " + company.getContact().getPhone() + "\n" +
                    resources.getString("email") + " " + company.getContact().getEmail();
        } catch (NoResultException e) {
            Platform.runLater(() -> notification.warning(resources.getString("need_fill_data_about_company")));
            return null;
        }
    }
}