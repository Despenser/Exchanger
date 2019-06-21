package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class SettingController implements Initializable {

    @FXML private Accordion accordion;

    private ProjectInformant informant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(SettingController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        initUploadCurrencies();
        initCurrencyRate();
        initCompanyInfo();
        initAboutDeveloper();
        initVersion();

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    private void initUploadCurrencies(){
        initTab("setting/tabUploadCurrencies");
    }

    private void initCurrencyRate() {
        initTab("setting/tabCurrencyRate");
    }

    private void initCompanyInfo() {
        initTab("setting/tabCompanyInfo");
    }

    private void initAboutDeveloper(){initTab("setting/tabAboutDeveloper");}

    private void initVersion(){
        initTab("setting/tabVersion");
    }

    private void initTab(String nameFXML) {
        informant.logInfo("Загрузка панели настроек " + nameFXML);
        List<Object> params = LoaderFXML.getInstance().loadSettingTitlePane(nameFXML);
        accordion.getPanes().add((TitledPane) params.get(0));
    }
}