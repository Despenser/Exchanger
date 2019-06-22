package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.jdom2.JDOMException;
import ru.golubyatnikov.money.exchange.model.util.ActualCurrency;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UploadCurrenciesController implements Initializable {

    private ActualCurrency actualCurrency;
    private ProjectInformant informant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualCurrency = ActualCurrency.getInstance();
        informant = new ProjectInformant(UploadCurrenciesController.class);
    }

    @FXML
    private void uploadForToday() {
        try {
            informant.logInfo("Запущен процесс загрузки курсов валют на текущий день");
            boolean isUpload = actualCurrency.uploadForToday();
            if (isUpload) informant.logInfoAndShowNotificationComplete("Актуальные курсы валют успешно загружены");
            else informant.logInfoAndShowNotificationInfo("Курсы валют не были загружены в базу, проверьте справочник валют на актуальность");
        } catch (IOException | JDOMException e) {
            informant.logErrorAndShowNotificationWarning("При загрузке курсов произошел сбой", e);
        }
    }

    @FXML
    private void uploadForMonth() {
        try {
            informant.logInfo("Запущен процесс загрузки курсов валют за текущий месяц");
            boolean isUpload = actualCurrency.uploadForMonth();
            if (isUpload) informant.logInfoAndShowNotificationComplete("Курсы валют за последний месяц успешно загружены");
            else informant.logInfoAndShowNotificationInfo("Курсы валют не были загружены в базу, проверьте справочник валют на актуальность");
        } catch (IOException | JDOMException e) {
            informant.logErrorAndShowNotificationWarning("При загрузке курсов произошел сбой", e);
        }
    }
}