package ru.golubyatnikov.money.exchange.controller.root;


import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class RootController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(RootController.class);

    @FXML private AnchorPane mainPane;
    @FXML private JFXHamburger hamburger;
    @FXML private JFXDrawer drawer;
    @FXML private Label labelEmployee;

    private Employee employee;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        employee =  LoaderFXML.getInstance().getMain().getCurrentEmployee();

        Platform.runLater(this::initUserLabel);
        Platform.runLater(this::initDrawer);

        LOG.info("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    AnchorPane getMainPane() {
        return mainPane;
    }

    private void initUserLabel(){
        labelEmployee.setText(resources.getString("user") + " " + employee.getSurname() + " " + employee.getName());
    }

    private void initDrawer() {
        LOG.info("Инициализация бокового меню");

        List<Object> params = LoaderFXML.getInstance().loadToolbar("root/toolbar");
        AnchorPane pane = (AnchorPane) params.get(0);
        ((ToolbarController) params.get(1)).setRootController(this);
        drawer.setSidePane(pane);

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);

        Platform.runLater(() -> {
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (Event event) -> drawer.toggle());
            drawer.setOnDrawerOpening((event) -> {
                task.setRate(task.getRate() * -1);
                task.play();
                drawer.toFront();
            });
            drawer.setOnDrawerClosed((event) -> {
                drawer.toBack();
                task.setRate(task.getRate() * -1);
                task.play();
            });
        });
    }

    @FXML
    private void logOut(MouseEvent event) {
        boolean result = Notification.getInstance().confirmation(resources.getString("close_session") + " " + employee.getSurname() + " " + employee.getName() + "?");
        if (result) {
            LOG.info("Производится выход из системы на окно авторизации");
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            LoaderFXML.getInstance().loadRoot(new Stage(), "login/loginPane", resources.getString("application_name"), 419, 367, false);
        }
    }
}