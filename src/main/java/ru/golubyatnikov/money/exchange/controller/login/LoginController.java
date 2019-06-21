package ru.golubyatnikov.money.exchange.controller.login;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.service.EmployeeService;
import ru.golubyatnikov.money.exchange.model.util.*;


public class LoginController implements Initializable {

    @FXML private TextField username;
    @FXML private PasswordField password;

    private static final String WRONG_FIELD = "wrong-fields";

    private Notification notification;
    private ProjectInformant informant;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;
    private String messageTooltip;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        informant = new ProjectInformant(LoginController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        messageTooltip = resources.getString("bad_credentials");
        notification = Notification.getInstance();
        loaderFXML = LoaderFXML.getInstance();

        username.textProperty().addListener((observable, oldValue, newValue) -> username.getStyleClass().remove(WRONG_FIELD));
        password.textProperty().addListener((observable, oldValue, newValue) -> password.getStyleClass().remove(WRONG_FIELD));

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    @FXML
    private void goAuth(ActionEvent event) {
        if (!username.getText().isEmpty() || !password.getText().isEmpty()) {
            informant.logInfo("Попытка авторизации в системе");
            try {
                Employee employee = new EmployeeService().findByLogin(username.getText());
                tryAuthInSystem(event, employee);
            } catch (NullPointerException e) {
                informant.logInfo("Авторизация не пройдена");
                setWrongStyleFields();
                setTooltips();
            }
        }
    }

    private void tryAuthInSystem(ActionEvent event, Employee employee){
        if (employee.getAuth().getPassword().equals(password.getText())) {
            informant.logInfo("Авторизация, выполнена успешно");
            setIsAdmin(employee);
            setCurrentEmployee(employee);
            closeAuthWindow(event);
            loadMainWindow();
        } else throw new NullPointerException();
    }

    private void setIsAdmin(Employee employee){
        String employeeRole = employee.getRole().getType();
        String adminRole = loaderFXML.getMain().getRoles().get(0).getType();

        if (employeeRole.equals(adminRole)) loaderFXML.getMain().setAdmin(true);
        else loaderFXML.getMain().setAdmin(false);
    }

    private void closeAuthWindow(ActionEvent event){
        informant.logInfo("Закрытие окна авторизации");
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private void setCurrentEmployee(Employee employee) {
        loaderFXML.getMain().setCurrentEmployee(employee);
    }

    private void loadMainWindow(){
        informant.logInfo("Запускается основное окно системы");
        loaderFXML.loadRoot(new Stage(), "root/rootPane",
                resources.getString("application_name"), 1024, 768, true);
        informant.logInfo("Основное окно системы запущено");
    }

    private void setWrongStyleFields() {
        username.getStyleClass().add(WRONG_FIELD);
        password.getStyleClass().add(WRONG_FIELD);
    }

    private void setTooltips() {
        username.setTooltip(notification.tooltip(messageTooltip));
        password.setTooltip(notification.tooltip(messageTooltip));
    }

    @FXML
    private void cancelAuth() {
        informant.logInfo("Пользователь отказался от прохождения авторизации, приложение прекращает работу");
        loaderFXML.getMain().stop();
    }
}