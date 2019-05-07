package ru.golubyatnikov.money.exchange.controller.employee;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.golubyatnikov.money.exchange.controller.AbstractController;
import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.entity.Operation;
import ru.golubyatnikov.money.exchange.model.service.EmployeeService;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.Report;
import java.net.URL;
import java.util.ResourceBundle;


public class EmployeeController extends AbstractController implements Initializable {

    @FXML private TableView<Employee> tableView;
    @FXML private DatePicker datePickerBirthday, datePickerReleased;
    @FXML private Button addEmployee, viewEmployee, editEmployee, deleteEmployee, archiveEmployee, reportEmployee, clearSearch;
    @FXML private TableColumn<Employee, Long> colSerial, colNumber;
    @FXML private TableColumn<Employee, String> colSurname, colName, colMiddleName, colGender, colRole, colLogin, colEmail, colPhone, colStatus;
    @FXML private TextField txtFieldSearch, txtFieldSurname, txtFieldName, txtFieldMiddleName, txtFieldGender, txtFieldRole, txtFieldSerialPass,
            txtFieldNumPass, txtFieldReleasedBy, txtFieldUnitCode, txtFieldBirthPlace, txtFieldRegistration, txtFieldPhone, txtFieldEmail;

    private EmployeeService employeeService;
    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        employeeService = new EmployeeService();
        notification = Notification.getInstance();
        loaderFXML = LoaderFXML.getInstance();

        datePickerBirthday.setOnMouseClicked(e -> { if (!datePickerBirthday.isEditable()) datePickerBirthday.hide(); });
        datePickerReleased.setOnMouseClicked(e -> { if (!datePickerReleased.isEditable()) datePickerReleased.hide(); });

        tableView.setPlaceholder(new Label(resources.getString("table_view_placeholder_employee")));

        addEmployee.setTooltip(notification.tooltip(resources.getString("add_employee")));
        viewEmployee.setTooltip(notification.tooltip(resources.getString("view_employee")));
        editEmployee.setTooltip(notification.tooltip(resources.getString("edit_employee")));
        deleteEmployee.setTooltip(notification.tooltip(resources.getString("delete_employee")));
        archiveEmployee.setTooltip(notification.tooltip(resources.getString("change_employee_status")));
        reportEmployee.setTooltip(notification.tooltip(resources.getString("report_employee")));
        clearSearch.setTooltip(notification.tooltip(resources.getString("clear_search")));

        colSurname.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colMiddleName.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().getGender().typeProperty());
        colSerial.setCellValueFactory(cellData -> cellData.getValue().getPassport().serialProperty().asObject());
        colNumber.setCellValueFactory(cellData -> cellData.getValue().getPassport().numberProperty().asObject());
        colRole.setCellValueFactory(cellData -> cellData.getValue().getRole().typeProperty());
        colLogin.setCellValueFactory(cellData -> cellData.getValue().getAuth().loginProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().getContact().phoneProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().getContact().emailProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().getStatus().nameProperty());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> show(newValue));

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> elasticSearch());

        Platform.runLater(this::getAll);
    }

    TableView<Employee> getTableView() {
        return tableView;
    }

    @Override
    public void add(ActionEvent event) {
        openHandler(resources.getString("form_create_employee"), event);
    }

    @Override
    public void view(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_view_employee")))
            openHandler(resources.getString("form_view_employee"), event);
    }

    @Override
    public void edit(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_edit_employee")))
            openHandler(resources.getString("form_edit_employee"), event);
    }

    @Override
    public void delete() {
        if (checkSelected(tableView, resources.getString("select_for_delete_employee"))) {
            Employee employee = tableView.getSelectionModel().getSelectedItem();
            if (employee.getOperations().isEmpty()) {
                boolean result = notification.confirmation(resources.getString("do_delete_employee"));
                if (result) delete(employee, employeeService, loaderFXML.getMain().getEmployees(), tableView);
            } else {
                boolean result = notification.confirmation(resources.getString("employee_has_operations"));
                if (result) {
                    for (Operation operation : employee.getOperations()) operation.getCurrencies().clear();
                    delete(employee, employeeService, loaderFXML.getMain().getEmployees(), tableView);
                }
            }
        }
    }

    @Override
    public void openHandler(String title, ActionEvent event) {
        EmployeeHandlerController controller = loaderFXML.loadModalWindow("employee/editorEmployee", title, false);
        controller.setController(this);
        controller.setEvent(event);
    }

    @FXML
    private void archive() {
        if (checkSelected(tableView, resources.getString("select_for_archive_employee"))) {
//            Employee employee = tableView.getSelectionModel().getSelectedItem();
//            if (employee.getStatus().getName().equals(loaderFXML.getMain().getStatuses().get(0).getName())) {
//                updateStatus(employee, employeeService, loaderFXML.getMain().getStatuses(),
//                        tableView, resources.getString("change_employee_status_on_archive"), 1);
//            } else updateStatus(employee, employeeService, loaderFXML.getMain().getStatuses(),
//                    tableView, resources.getString("change_employee_status_on_active"), 0);
        }
    }

    @Override
    public void report() {
        if (checkSelected(tableView, resources.getString("select_for_report_employee")))
            Platform.runLater(() -> Report.getInstance().employeeReport(tableView.getSelectionModel().getSelectedItem()));
    }

    @Override
    public void clearSearch() {
        txtFieldSearch.clear();
    }

    @Override
    public void getAll() {
        populateTable(tableView, loaderFXML.getMain().getEmployees());
    }

    @Override
    public void elasticSearch() {
        if (txtFieldSearch.getText().isEmpty()) getAll();
        ObservableList<Employee> employees = FXCollections.observableArrayList(employeeService.findEmployeePane(txtFieldSearch.getText()));
        if (employees.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("new_table_view_placeholder_employee")));
        loaderFXML.getMain().setEmployees(employees);
        populateTable(tableView, loaderFXML.getMain().getEmployees());
    }

    private void show(Employee employee) {
        if (employee != null) {
            txtFieldSurname.setText(employee.getSurname());
            txtFieldName.setText(employee.getName());
            txtFieldMiddleName.setText(employee.getMiddleName());
            txtFieldGender.setText(employee.getGender().getType());
            datePickerBirthday.setValue(employee.getBirthday());
            txtFieldSerialPass.setText(String.valueOf(employee.getPassport().getSerial()));
            txtFieldNumPass.setText(String.valueOf(employee.getPassport().getNumber()));
            txtFieldUnitCode.setText(String.valueOf(employee.getPassport().getUnitCode()));
            txtFieldReleasedBy.setText(employee.getPassport().getReleasedBy());
            txtFieldBirthPlace.setText(employee.getPassport().getBirthPlace());
            txtFieldRegistration.setText(employee.getPassport().getRegistration());
            datePickerReleased.setValue(employee.getPassport().getDateReleased());
            txtFieldRole.setText(employee.getRole().getType());
            txtFieldPhone.setText(employee.getContact().getPhone());
            txtFieldEmail.setText(employee.getContact().getEmail());
        }
    }
}