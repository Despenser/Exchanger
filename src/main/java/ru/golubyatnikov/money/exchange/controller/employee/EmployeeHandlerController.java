package ru.golubyatnikov.money.exchange.controller.employee;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.golubyatnikov.money.exchange.controller.AbstractModalController;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.enumirate.Mode;
import ru.golubyatnikov.money.exchange.model.enumirate.StateColor;
import ru.golubyatnikov.money.exchange.model.service.EmployeeService;
import ru.golubyatnikov.money.exchange.model.util.*;

import java.net.URL;
import java.util.ResourceBundle;


public class EmployeeHandlerController extends AbstractModalController implements Initializable {

    @FXML private Button btnAction;
    @FXML private Label labelTitle, labelStatus;
    @FXML private GridPane gridPaneFirst, gridPaneSecond;
    @FXML private ComboBox<String> comboBoxGender, comboBoxRole;
    @FXML private DatePicker datePickerBirthday, datePickerReleased;
    @FXML private TextArea txtAreaBirthPlace, txtAreaRegistration, txtAreaReleasedBy;
    @FXML private PasswordField passFieldPass, passFieldPassAgain;
    @FXML private TextField txtFieldSurname, txtFieldName, txtFieldMiddleName, txtFieldSerialPass,
            txtFieldNumPass, txtFieldUnitCode, txtFieldPhone, txtFieldEmail, txtFieldLogin;

    private EmployeeController employeeController;
    private ActionEvent event;
    private ProjectInformant informant;
    private Notification notification;
    private EmployeeService employeeService;
    private LoaderFXML loaderFXML;
    private Validator validator;
    private ResourceBundle resources;
    private Employee employee;
    private Mode mode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(EmployeeHandlerController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;

        loaderFXML = LoaderFXML.getInstance();
        validator = Validator.getInstance();
        notification = Notification.getInstance();
        employeeService = new EmployeeService();

        for (Gender type : loaderFXML.getMain().getGenders()) comboBoxGender.getItems().addAll(type.getType());
        for (Role role : loaderFXML.getMain().getRoles()) comboBoxRole.getItems().addAll(role.getType());

        Platform.runLater(this::checkMode);
        Platform.runLater(() -> { if (mode != Mode.VIEW) setValidationOnPane();});

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }

    public void setController(EmployeeController employeeController) {
        this.employeeController = employeeController;
    }

    @Override
    public void checkMode() {
        switch (((Button) event.getSource()).getId()) {
            case "addEmployee":
                informant.logInfo("Запущен режим заведения сотрудника");
                mode = Mode.CREATE;
                labelTitle.setText(resources.getString("title_create_employee"));
                btnAction.setText(resources.getString("button_create"));
                labelStatus.setText(resources.getString("creation_status"));
                labelStatus.getStyleClass().add(StateColor.NEW.getState());
                employee = new Employee();
                break;
            case "editEmployee":
                informant.logInfo("Запущен режим редактирования сотрудника");
                mode = Mode.EDIT;
                labelTitle.setText(resources.getString("title_edit_employee"));
                btnAction.setText(resources.getString("button_edit"));
                employee = employeeController.getTableView().getSelectionModel().getSelectedItem();
                showDetails();
                break;
            default:
                mode = Mode.VIEW;
                informant.logInfo("Запущен режим просмотра сотрудника");
                labelTitle.setText(resources.getString("title_view_employee"));
                btnAction.setText(resources.getString("button_view"));
                employee = employeeController.getTableView().getSelectionModel().getSelectedItem();
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
    public Employee create(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("is_input_data_correct"))) {
                Employee employee = contain();
                if (employeeService.findByLoginWithoutStatus(employee.getAuth().getLogin()) == null) {
                    if (employeeService.findBySerialNumber(employee.getPassport().getSerial(), employee.getPassport().getNumber()) == null) {
                        informant.logInfo("Создание нового сотрудника");
                        employeeService.create(employee);
                        loaderFXML.getMain().getEmployees().add(employee);
                        informant.logInfoAndShowNotificationComplete(resources.getString("new_employee") + " " + employee.getSurname() + " " + employee.getName() + " " +
                                resources.getString("successfully_created"));
                        closeWindow(event);
                        return employee;
                    } else informant.logInfoAndShowNotificationWarning(resources.getString("employee_with_this_passport_exist"));
                } else informant.logInfoAndShowNotificationWarning(resources.getString("employee_with_this_login_exist"));
            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
        return null;
    }

    @Override
    public void update(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("do_change_employee_data"))) {
                if (employeeService.findByIdAndLoginWithoutStatus(employee.getId(), txtFieldLogin.getText()) == null) {
                    if (employeeService.findByIdAndSerialNumber(employee.getId(), Long.parseLong(txtFieldSerialPass.getText()), Long.parseLong(txtFieldNumPass.getText())) == null) {
                        contain();
                        informant.logInfo("Редактирование сотрудника с id " + employee.getId());
                        employeeService.update(employee);
                        employeeController.getTableView().refresh();
                        informant.logInfoAndShowNotificationComplete(resources.getString("information_about_employee_successfully_updated"));
                        closeWindow(event);
                    } else informant.logInfoAndShowNotificationWarning(resources.getString("employee_with_this_passport_exist"));
                } else informant.logInfoAndShowNotificationWarning(resources.getString("employee_with_this_login_exist"));
            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
    }

    @Override
    public void report() {
        Employee employee = employeeController.getTableView().getSelectionModel().getSelectedItem();
        informant.logInfo("Запущена генерация отчета по сотруднику с id " + employee.getId());
        Report.getInstance().employeeReport(employee);
    }

    @Override
    public Employee contain() {
        employee.setSurname(txtFieldSurname.getText());
        employee.setName(txtFieldName.getText());
        employee.setMiddleName(txtFieldMiddleName.getText());
        employee.setBirthday(datePickerBirthday.getValue());
        employee.setGender(loaderFXML.getMain().getGenders().get(comboBoxGender.getSelectionModel().getSelectedIndex()));
        employee.setRole(loaderFXML.getMain().getRoles().get(comboBoxRole.getSelectionModel().getSelectedIndex()));

        Contact contact;
        if (mode == Mode.CREATE) contact = new Contact();
        else contact = employee.getContact();
        contact.setPhone(txtFieldPhone.getText());
        contact.setEmail(txtFieldEmail.getText());
        employee.setContact(contact);

        Passport passport;
        if (mode == Mode.CREATE) passport = new Passport();
        else passport = employee.getPassport();
        passport.setSerial(Long.parseLong(txtFieldSerialPass.getText()));
        passport.setNumber(Long.parseLong(txtFieldNumPass.getText()));
        passport.setUnitCode(Integer.parseInt(txtFieldUnitCode.getText()));
        passport.setBirthPlace(txtAreaBirthPlace.getText());
        passport.setReleasedBy(txtAreaReleasedBy.getText());
        passport.setDateReleased(datePickerReleased.getValue());
        passport.setRegistration(txtAreaRegistration.getText());
        employee.setPassport(passport);

        Auth auth;
        if (mode == Mode.CREATE) auth = new Auth();
        else auth = employee.getAuth();
        auth.setLogin(txtFieldLogin.getText());
        auth.setPassword(passFieldPass.getText());
        employee.setAuth(auth);

        Status status;
        if (mode == Mode.CREATE) status = loaderFXML.getMain().getStatuses().get(0);
        else status = employee.getStatus();
        employee.setStatus(status);

        return employee;
    }

    @Override
    public void showDetails() {
        labelStatus.setText(employee.getStatus().getName());
        txtFieldSurname.setText(employee.getSurname());
        txtFieldName.setText(employee.getName());
        txtFieldMiddleName.setText(employee.getMiddleName());
        comboBoxGender.getSelectionModel().select(employee.getGender().getType());
        datePickerBirthday.setValue(employee.getBirthday());
        txtFieldSerialPass.setText(String.valueOf(employee.getPassport().getSerial()));
        txtFieldNumPass.setText(String.valueOf(employee.getPassport().getNumber()));
        txtFieldUnitCode.setText(String.valueOf(employee.getPassport().getUnitCode()));
        txtAreaReleasedBy.setText(employee.getPassport().getReleasedBy());
        txtAreaBirthPlace.setText(employee.getPassport().getBirthPlace());
        txtAreaRegistration.setText(employee.getPassport().getRegistration());
        datePickerReleased.setValue(employee.getPassport().getDateReleased());
        txtFieldPhone.setText(employee.getContact().getPhone());
        txtFieldEmail.setText(employee.getContact().getEmail());
        comboBoxRole.getSelectionModel().select(employee.getRole().getType());
        txtFieldLogin.setText(employee.getAuth().getLogin());
        passFieldPass.setText(employee.getAuth().getPassword());
        passFieldPassAgain.setText(employee.getAuth().getPassword());

        if (employee.getStatus().getName().equals(loaderFXML.getMain().getStatuses().get(0).getName())) {
            labelStatus.getStyleClass().add(StateColor.ACTIVE.getState());
        } else labelStatus.getStyleClass().add(StateColor.ARCHIVE.getState());
    }

    @Override
    public void setValidationOnPane() {
        //TODO валидация телефона
        validator.validatePane(btnAction, gridPaneFirst, gridPaneSecond);
        validator.cyrillicAndDash(resources.getString("prompt_surname"), txtFieldSurname);
        validator.onlyCyrillic(resources.getString("prompt_name"), txtFieldName);
        validator.onlyCyrillic(resources.getString("prompt_middle_name"), txtFieldMiddleName);
        validator.comboBoxNotNull(comboBoxGender);
        validator.checkBirthdayAndPassport(resources.getString("prompt_birth_day"), resources.getString("prompt_date_released"), datePickerBirthday, datePickerReleased);
        //validator.checkPhoneNumber(resources.getString("prompt_phone"), txtFieldPhone);
        validator.email(resources.getString("prompt_email"), txtFieldEmail);
        validator.comboBoxNotNull(comboBoxRole);
        validator.latinAndInteger(resources.getString("prompt_login"), txtFieldLogin);
        validator.checkEqualsPasswords(resources.getString("prompt_password"), 6, passFieldPass, passFieldPassAgain);
        validator.onlyInteger(resources.getString("prompt_serial"), txtFieldSerialPass, 4);
        validator.onlyInteger(resources.getString("prompt_number"), txtFieldNumPass, 6);
        validator.onlyInteger(resources.getString("prompt_unit_code"), txtFieldUnitCode, 6);
        validator.cyrillicAndInteger(resources.getString("prompt_released_by"), txtAreaReleasedBy);
        validator.cyrillicAndInteger(resources.getString("prompt_birth_place"), txtAreaBirthPlace);
        validator.cyrillicAndInteger(resources.getString("prompt_registration"), txtAreaRegistration);
    }
}