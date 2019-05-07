package ru.golubyatnikov.money.exchange.controller.login;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.service.EmployeeService;
import ru.golubyatnikov.money.exchange.model.util.*;


public class LoginController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(LoginController.class);

    @FXML private TextField username;
    @FXML private PasswordField password;

    private static final String WRONG_FIELD = "wrong-fields";

    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;
    private String messageTooltip;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        LOG.info("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        messageTooltip = resources.getString("bad_credentials");
        notification = Notification.getInstance();
        loaderFXML = LoaderFXML.getInstance();

        username.textProperty().addListener((observable, oldValue, newValue) -> username.getStyleClass().remove(WRONG_FIELD));
        password.textProperty().addListener((observable, oldValue, newValue) -> password.getStyleClass().remove(WRONG_FIELD));

        LOG.info("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    @FXML
    private void goAuth(ActionEvent event) {
        if (!username.getText().isEmpty() || !password.getText().isEmpty()) {
            try {
                Employee employee = new EmployeeService().findByLogin(username.getText());
                if (employee.getAuth().getPassword().equals(password.getText())) {
                    LOG.info("Авторизация, выполнена успешно");
                    if (employee.getRole().getType().equals(loaderFXML.getMain().getRoles().get(0).getType())) loaderFXML.getMain().setAdmin(true);
                    else loaderFXML.getMain().setAdmin(false);

                    LOG.info("Запускается основное окно системы");
                    ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                    loaderFXML.getMain().setCurrentEmployee(employee);
                    loaderFXML.loadRoot(new Stage(), "root/rootPane",
                            resources.getString("application_name"), 1024, 768, true);
                } else throw new NullPointerException();
            } catch (NullPointerException e) {
                LOG.info("Авторизация не пройдена");
                username.getStyleClass().add(WRONG_FIELD);
                password.getStyleClass().add(WRONG_FIELD);
                username.setTooltip(notification.tooltip(messageTooltip));
                password.setTooltip(notification.tooltip(messageTooltip));
            }
        }
    }

    @FXML
    private void cancelAuth() {
        LOG.info("Отказ от прохождения авторизации, приложение прекращает работу");
        Platform.exit();
    }
}