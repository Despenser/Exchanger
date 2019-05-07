package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class SettingController implements Initializable {

    @FXML private Accordion accordion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUploadCurrencies();
        initCurrencyRate();
        initCompanyInfo();
        initAboutDeveloper();
        initVersion();
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
        List<Object> params = LoaderFXML.getInstance().loadSettingTitlePane(nameFXML);
        accordion.getPanes().add((TitledPane) params.get(0));
    }
}