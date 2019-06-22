package ru.golubyatnikov.money.exchange.controller.operation;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.golubyatnikov.money.exchange.controller.AbstractController;
import ru.golubyatnikov.money.exchange.model.entity.Operation;
import ru.golubyatnikov.money.exchange.model.service.OperationService;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import ru.golubyatnikov.money.exchange.model.util.Report;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;


public class OperationController extends AbstractController implements Initializable {

    @FXML private ToolBar toolbar;
    @FXML private Button addOperation, viewOperation, editOperation, deleteOperation, reportOperation, clearSearch;
    @FXML private TableView<Operation> tableView;
    @FXML private TableColumn<Operation, Long> colCode;
    @FXML private TableColumn<Operation, Float> colRate;
    @FXML private TableColumn<Operation, LocalDate> colDate;
    @FXML private TableColumn<Operation, LocalTime> colTime;
    @FXML private TableColumn<Operation, String> colSumIn, colSumOut;
    @FXML private TableColumn<Operation, String> colType, colSurname, colName, colMiddleName, colInCurrency, colOutCurrency;
    @FXML private TableColumn<Operation, Long> colClientSerial, colClientNum;
    @FXML private TextField txtFieldSearch;

    private ObservableList<Operation> operations;
    private OperationService operationService;
    private ProjectInformant informant;
    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(OperationController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        operationService = new OperationService();
        notification = Notification.getInstance();
        loaderFXML = LoaderFXML.getInstance();
        operations = FXCollections.observableArrayList(operationService.findAll());

        if (loaderFXML.getMain().isNotAdmin()) toolbar.getItems().removeAll(editOperation, deleteOperation);
        if (loaderFXML.getMain().getCurrentEmployee().getRole().getType().equals(loaderFXML.getMain().getRoles().get(2).getType())){
            toolbar.getItems().removeAll(addOperation, editOperation, deleteOperation);
        }

        tableView.setPlaceholder(new Label(resources.getString("table_view_placeholder_operation")));

        addOperation.setTooltip(notification.tooltip(resources.getString("add_operation")));
        viewOperation.setTooltip(notification.tooltip(resources.getString("view_operation")));
        editOperation.setTooltip(notification.tooltip(resources.getString("edit_operation")));
        deleteOperation.setTooltip(notification.tooltip(resources.getString("delete_operation")));
        reportOperation.setTooltip(notification.tooltip(resources.getString("report_operation")));
        clearSearch.setTooltip(notification.tooltip(resources.getString("clear_search")));

        colCode.setCellValueFactory(cellData -> cellData.getValue().codeOperationProperty().asObject());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateOperationProperty());
        colTime.setCellValueFactory(cellData -> cellData.getValue().timeOperationProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().getTypeOperation().typeProperty());
        colRate.setCellValueFactory(cellData -> cellData.getValue().rateProperty().asObject());
        colClientSerial.setCellValueFactory(cellData -> cellData.getValue().getClient().getPassport().serialProperty().asObject());
        colClientNum.setCellValueFactory(cellData -> cellData.getValue().getClient().getPassport().numberProperty().asObject());
        colSurname.setCellValueFactory(cellData -> cellData.getValue().getClient().surnameProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().getClient().nameProperty());
        colMiddleName.setCellValueFactory(cellData -> cellData.getValue().getClient().middleNameProperty());
        colOutCurrency.setCellValueFactory(cellData -> cellData.getValue().codeOutProperty());
        colSumOut.setCellValueFactory(cellData -> cellData.getValue().sumOutProperty());
        colInCurrency.setCellValueFactory(cellData -> cellData.getValue().codeInProperty());
        colSumIn.setCellValueFactory(cellData -> cellData.getValue().sumInProperty());

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> elasticSearch());

        Platform.runLater(this::getAll);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    TableView<Operation> getTableView() {
        return tableView;
    }

    ObservableList<Operation> getOperations() {
        return operations;
    }

    private void setOperations(ObservableList<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public void add(ActionEvent event) {
        informant.logInfo("Открытие формы заведения операции");
        openHandler(resources.getString("form_create_operation"), event);
    }

    @Override
    public void view(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_view_operation"))) {
            informant.logInfo("Открытие формы просмотра операции");
            openHandler(resources.getString("form_view_operation"), event);
        }
    }

    @Override
    public void edit(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_edit_operation"))) {
            informant.logInfo("Открытие формы редактирования операции");
            openHandler(resources.getString("form_edit_operation"), event);
        }

    }

    @Override
    public void delete() {
        if (checkSelected(tableView, resources.getString("select_for_delete_operation"))) {
            Operation operation = tableView.getSelectionModel().getSelectedItem();
            boolean result = notification.confirmation(resources.getString("do_delete_operation") + " " + operation.getCode() + " ?");
            if (result) {
                informant.logInfo("Запущена процедура удаления операции с id " + operation.getId() + ", клиент: " +
                        operation.getClient().getSurname() + " " + operation.getClient().getName());
                operation.getCurrencies().clear();
                delete(operation, operationService, operations, tableView);
            }
        }
    }

    @Override
    public void openHandler(String title, ActionEvent event) {
        OperationHandlerController controller = loaderFXML.loadModalWindow("operation/editorOperation", title, false);
        controller.setOperationController(this);
        controller.setEvent(event);
    }

    @Override
    public void report() {
        if (checkSelected(tableView, resources.getString("select_for_report_operation"))) {
            Operation operation = tableView.getSelectionModel().getSelectedItem();
            informant.logInfo("Запущена генерация отчета по операции с id " + operation.getId());
            Platform.runLater(() -> Report.getInstance().operationReport(resources.getString("statement_of_operation") + " " +
                            operation.getCode(), operation.getClient(), operation.getEmployee(), operation));
        }
    }

    @Override
    public void clearSearch() {
        txtFieldSearch.clear();
    }

    @Override
    public void getAll() {
        populateTable(tableView, operations);
    }

    @Override
    public void elasticSearch() {
        if (txtFieldSearch.getText().isEmpty()) getAll();
        ObservableList<Operation> operations = FXCollections.observableArrayList(operationService.find(txtFieldSearch.getText()));
        if (operations.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("new_table_view_placeholder_operation")));
        setOperations(operations);
        populateTable(tableView, operations);
    }
}