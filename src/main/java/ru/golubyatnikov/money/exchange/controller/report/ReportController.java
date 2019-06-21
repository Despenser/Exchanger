package ru.golubyatnikov.money.exchange.controller.report;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import ru.golubyatnikov.money.exchange.model.entity.Operation;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import ru.golubyatnikov.money.exchange.model.service.OperationService;
import ru.golubyatnikov.money.exchange.model.util.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


public class ReportController implements Initializable {

    @FXML private ComboBox <IsoCode> comboBoxCurrencies;
    @FXML private DatePicker specifiedDate, dateWith, dateBy;

    private Notification notification;
    private ProjectInformant informant;
    private OperationService operationService;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;
    private Report report;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(ReportController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        notification = Notification.getInstance();
        loaderFXML =  LoaderFXML.getInstance();
        operationService = new OperationService();
        report = Report.getInstance();

        ConfigComboBox.configComboBox(comboBoxCurrencies);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    @FXML
    private void todayReport() {
        informant.logInfo("Запущена процедура получения ежедневного отчета");
        LocalDate now = LocalDate.now();
        List<Operation> operations = operationService.todayReport(now);
        if (!operations.isEmpty()) {
            report.operationListReport(resources.getString("today_report"), loaderFXML.getMain().getCurrentEmployee(), operations);
            informant.logInfo("Ежедневный отчет сгенерирован");
        }
        else informant.logInfoAndShowNotificationWarning(resources.getString("operations_for_current_day_not_found"));

    }

    @FXML
    private void specifiedDayReport() {
        LocalDate date = specifiedDate.getValue();
        if (date != null){
            informant.logInfo("Запущена процедура получения отчета на " + date);
            if (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                List<Operation> operations = operationService.todayReport(date);
                if (operations.isEmpty()) {
                    informant.logInfoAndShowNotificationWarning(resources.getString("operations_for_select_day_not_found"));
                }
                else {
                    report.operationListReport(resources.getString("report_on_operations_for") + " " +
                            DateEditor.formatLocalDateToString(date, DatePattern.PATTERN_DOT), loaderFXML.getMain().getCurrentEmployee(), operations);
                    informant.logInfo("Отчет на " + date + " сгенерирован");
                }
            }
            else notification.warning(resources.getString("selected_date_must_be_less_than_or_equal_to_current_date"));
        } else notification.warning(resources.getString("you_must_select_date_to_generate_report"));
    }

    @FXML
    private void selectedCurrencyReport() {
        IsoCode currency = comboBoxCurrencies.getValue();
        LocalDate from = dateWith.getValue();
        LocalDate till = dateBy.getValue();
        if (from != null && till != null){
            if (from.isBefore(till)) {
                informant.logInfo("Запущена процедура получения отчета по валюте " + currency.name() + " за период с " + from + " по " + till);
                List<Operation> operations = operationService.operationsByCurrencyCode(currency.name(), from, till);
                if (operations.isEmpty()) {
                    informant.logInfoAndShowNotificationWarning(resources.getString("operations_for_period_by_currency"));
                }
                else report.operationListReport(resources.getString("report_on_operations_for_period") + " " +
                        DateEditor.formatLocalDateToString(from, DatePattern.PATTERN_DOT) + " " +
                        resources.getString("till") + " " +
                        DateEditor.formatLocalDateToString(till, DatePattern.PATTERN_DOT) + "  " +
                        resources.getString("currency") + " " + currency.name(),
                        loaderFXML.getMain().getCurrentEmployee(), operations);

                informant.logInfo("Отчет по валюте " + currency.name() + " за период с " + from + " по " + till + " сгенерирован");
            }
            else if (from.isEqual(till)) notification.warning(resources.getString("specified_period_must_be_more_than_one_day"));
            else notification.warning("end_date_of_period_must_be_greater_than_initial");
        }
        else notification.warning(resources.getString("to_generate_report_you_must_specify_periods"));
    }
}