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
import ru.golubyatnikov.money.exchange.model.entity.Status;
import ru.golubyatnikov.money.exchange.model.service.EmployeeService;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import ru.golubyatnikov.money.exchange.model.util.Report;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


public class EmployeeController extends AbstractController implements Initializable {

    @FXML private TableView<Employee> tableView;
    @FXML private DatePicker datePickerBirthday, datePickerReleased;
    @FXML private Button addEmployee, viewEmployee, editEmployee, deleteEmployee, archiveEmployee, reportEmployee, clearSearch;
    @FXML private TableColumn<Employee, Long> colSerial, colNumber;
    @FXML private TableColumn<Employee, String> colSurname, colName, colMiddleName, colGender, colRole, colLogin, colEmail, colPhone, colStatus;
    @FXML private TextField txtFieldSearch, txtFieldSurname, txtFieldName, txtFieldMiddleName, txtFieldGender, txtFieldRole, txtFieldSerialPass,
            txtFieldNumPass, txtFieldReleasedBy, txtFieldUnitCode, txtFieldBirthPlace, txtFieldRegistration, txtFieldPhone, txtFieldEmail;

    private EmployeeService employeeService;
    private ProjectInformant informant;
    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(EmployeeController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

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

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    TableView<Employee> getTableView() {
        return tableView;
    }

    @Override
    public void add(ActionEvent event) {
        informant.logInfo("Открытие формы заведения сотрудника");
        openHandler(resources.getString("form_create_employee"), event);
    }

    @Override
    public void view(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_view_employee"))) {
            informant.logInfo("Открытие формы просмотра сотрудника");
            openHandler(resources.getString("form_view_employee"), event);
        }
    }

    @Override
    public void edit(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_edit_employee"))) {
            informant.logInfo("Открытие формы редактирования сотрудника");
            openHandler(resources.getString("form_edit_employee"), event);
        }
    }

    @Override
    public void delete() {
        if (checkSelected(tableView, resources.getString("select_for_delete_employee"))) {
            Employee employee = tableView.getSelectionModel().getSelectedItem();
            if (employee.getOperations().isEmpty()) {
                boolean result = notification.confirmation(resources.getString("do_delete_employee"));
                if (result) {
                    informant.logInfo("Запущена процедура удаления сотрудника с id " + employee.getId() + ", без операций");
                    delete(employee, employeeService, loaderFXML.getMain().getEmployees(), tableView);
                }
            } else {
                boolean result = notification.confirmation(resources.getString("employee_has_operations"));
                if (result) {
                    Set<Operation> operations = employee.getOperations();
                    informant.logInfo("Запущена процедура удаления сотрудника с id " + employee.getId() + ", количество операций у сотрудника " + operations.size());
                    operations.forEach(operation -> operation.getCurrencies().clear());
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
            Employee employee = tableView.getSelectionModel().getSelectedItem();
            Status active = loaderFXML.getMain().getStatuses().get(0);
            Status archive = loaderFXML.getMain().getStatuses().get(1);

            if (employee.getStatus().getName().equals(active.getName())) {
                updateStatus(employee, employeeService, tableView, archive, resources.getString("change_employee_status_on_archive"));
                informant.logInfo("Смена статуса у сотрудника с id " + employee.getId() + " на \"В архиве\"");
            }
            else {
                updateStatus(employee, employeeService, tableView, active, resources.getString("change_employee_status_on_active"));
                informant.logInfo("Смена статуса у сотрудника с id " + employee.getId() + " на \"Активный\"");
            }
        }
    }

    @Override
    public void report() {
        if (checkSelected(tableView, resources.getString("select_for_report_employee"))) {
            Employee employee = tableView.getSelectionModel().getSelectedItem();
            informant.logInfo("Запущена генерация отчета по сотруднику с id " + employee.getId());
            Platform.runLater(() -> Report.getInstance().employeeReport(employee));
        }
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
            informant.logInfo("Фокус установлен на сотруднике с id " + employee.getId());
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