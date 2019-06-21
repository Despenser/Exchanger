package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.jdom2.JDOMException;
import ru.golubyatnikov.money.exchange.model.util.ActualCurrency;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//TODO Допилить контроллер
public class UploadCurrenciesController implements Initializable {

    private ActualCurrency actualCurrency;
    private Notification notification;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualCurrency = ActualCurrency.getInstance();
        notification = Notification.getInstance();
    }

    @FXML
    private void upload() {
        try {
            actualCurrency.upload();
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
            notification.warning("При загрузке курсов произошел сбой");
        }
    }

    @FXML
    private void uploadForMonth() {
        try {
            actualCurrency.uploadForMonth();
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
            notification.warning("При загрузке курсов произошел сбой");
        }
    }
}