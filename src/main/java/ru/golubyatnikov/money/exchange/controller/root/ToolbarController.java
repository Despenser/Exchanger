package ru.golubyatnikov.money.exchange.controller.root;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import java.net.URL;
import java.util.ResourceBundle;


public class ToolbarController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ToolbarController.class);

    @FXML private AnchorPane anchorPaneEmployee, anchorPaneSetting;
    @FXML private VBox slidePane;

    private RootController rootController;
    private LoaderFXML loaderFXML;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Инициализация класса " + this.getClass().getSimpleName());

        loaderFXML = LoaderFXML.getInstance();
        if (loaderFXML.getMain().isAdmin()) slidePane.getChildren().removeAll(anchorPaneEmployee, anchorPaneSetting);

        LOG.info("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    void setRootController(RootController rootController){
        this.rootController = rootController;
    }

    private void load(String nameFXML){
        LOG.info("Загрузка пункта меню " + nameFXML);
        loaderFXML.loadScene(nameFXML, rootController.getMainPane());
    }

    @FXML
    private void loadClient() {
        load("client/clientPane");
    }

    @FXML
    private void loadEmployees(){
        load("employee/employeePane");
    }

    @FXML
    private void loadCalculation() {
        load("calculation/calcPane");
    }

    @FXML
    private void loadOperation() {
        load("operation/operationPane");
    }

    @FXML
    private void loadReport() {
        load("report/reportPane");
    }

    @FXML
    private void loadSetting() {
        load("setting/settingPane");
    }
}