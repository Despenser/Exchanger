package ru.golubyatnikov.money.exchange.controller.root;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.net.URL;
import java.util.ResourceBundle;


public class ToolbarController implements Initializable {

    @FXML private AnchorPane anchorPaneEmployee, anchorPaneSetting;
    @FXML private VBox slidePane;

    private RootController rootController;
    private ProjectInformant informant;
    private LoaderFXML loaderFXML;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(ToolbarController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        loaderFXML = LoaderFXML.getInstance();
        if (loaderFXML.getMain().isNotAdmin())
            slidePane.getChildren().removeAll(anchorPaneEmployee, anchorPaneSetting);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    void setRootController(RootController rootController){
        this.rootController = rootController;
    }

    private void load(String nameFXML){
        informant.logInfo("Открытие пункта меню " + nameFXML);
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