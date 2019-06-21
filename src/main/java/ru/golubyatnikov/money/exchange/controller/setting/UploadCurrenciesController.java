package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.jdom2.JDOMException;
import ru.golubyatnikov.money.exchange.model.util.ActualCurrency;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//TODO Допилить контроллер
public class UploadCurrenciesController implements Initializable {

    private ActualCurrency actualCurrency;
    private ProjectInformant informant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualCurrency = ActualCurrency.getInstance();
        informant = new ProjectInformant(UploadCurrenciesController.class);
    }

    @FXML
    private void upload() {
        try {
            boolean isUpload = actualCurrency.upload();
            if (isUpload) informant.logInfoAndShowNotificationComplete("Актуальные курсы валют успешно загружены");
        } catch (IOException | JDOMException e) {
            informant.logErrorAndShowNotification("При загрузке курсов произошел сбой", e);
        }
    }

    @FXML
    private void uploadForMonth() {
        try {
            boolean isUpload = actualCurrency.uploadForMonth();
            if (isUpload) informant.logInfoAndShowNotificationComplete("Курсы валют за последний месяц успешно загружены");
        } catch (IOException | JDOMException e) {
            informant.logErrorAndShowNotification("При загрузке курсов произошел сбой", e);
        }
    }
}