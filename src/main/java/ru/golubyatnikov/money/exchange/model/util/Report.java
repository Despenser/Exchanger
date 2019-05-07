package ru.golubyatnikov.money.exchange.model.util;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import ru.golubyatnikov.money.exchange.controller.report.PreviewController;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.service.CompanyService;
import javax.persistence.NoResultException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Report {

    private static volatile Report instance;
    private static final String REPORT_PATH = "/report/";
    private static final String PEOPLE = "people.jasper";
    private static final String OPERATION = "operation.jasper";
    private static final String OPERATION_LIST = "operationList.jasper";

    public static Report getInstance() {
        if (instance == null) {
            synchronized (Report.class) {
                if (instance == null) instance = new Report();
            }
        }
        return instance;
    }

    private <T extends JRDataSource> void showReport(String jasperPattern, HashMap<String, Object> parameters, T collection) {
        if (parameters == null || collection == null) return;
        try {
            URL urlReport = Report.class.getResource(REPORT_PATH + jasperPattern);
            JasperReport report = (JasperReport) JRLoader.loadObject(urlReport);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, collection);
            PreviewController controller = LoaderFXML.getInstance().loadModalWindow("report/previewPane","Печатная форма", true);
            controller.setJasperPrint(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void clientReport(Client client) {
        showReport(PEOPLE, getCompanyData("Анкета Клиента"), getData(client));
    }

    public void employeeReport(Employee employee) {
        showReport(PEOPLE, getCompanyData("Анкета Сотрудника"), getData(employee));
    }

    public void operationReport(String title, Client client, Employee employee, Operation operation){
        HashMap<String, Object> parameters = getCompanyData(title);
        if (parameters == null) return;
        parameters.put("serialClient", client.getPassport().getSerial());
        parameters.put("numberClient", client.getPassport().getNumber());
        parameters.put("surnameClient", client.getSurname());
        parameters.put("nameClient", client.getName());
        parameters.put("middleNameClient", client.getMiddleName());
        parameters.put("surnameEmployee", employee.getSurname() );
        parameters.put("nameEmployee", employee.getName());
        parameters.put("middleNameEmployee", employee.getMiddleName());
        parameters.put("ItemDataSource", new JRBeanCollectionDataSource(Collections.singleton(operation)));
        showReport(OPERATION, parameters, new JREmptyDataSource());
    }

    public void operationListReport(String title, Employee employee, List<Operation> operationList){
        HashMap<String, Object> parameters = getCompanyData(title);
        if (parameters == null) return;
        parameters.put("surnameEmployee", employee.getSurname() );
        parameters.put("nameEmployee", employee.getName());
        parameters.put("middleNameEmployee", employee.getMiddleName());
        parameters.put("ItemDataSource", new JRBeanCollectionDataSource(operationList));
        showReport(OPERATION_LIST, parameters, new JREmptyDataSource());
    }

    private JRBeanCollectionDataSource getData(Client client){
        return getData(client.getSurname(), client.getName(), client.getMiddleName(), client.getBirthday(), client.getPassport(), client.getContact());
    }

    private JRBeanCollectionDataSource getData(Employee employee){
        return getData(employee.getSurname(), employee.getName(), employee.getMiddleName(), employee.getBirthday(), employee.getPassport(), employee.getContact());
    }

    private JRBeanCollectionDataSource getData(String surname, String name, String middleName, LocalDate birthday, Passport passport, Contact contact) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("surname", surname);
        parameters.put("name", name);
        parameters.put("middleName", middleName);
        parameters.put("birthday", DateEditor.formatLocalDateToString(birthday, DatePattern.PATTERN_DOT));
        parameters.put("serial", passport.getSerial());
        parameters.put("number", passport.getNumber());
        parameters.put("unitCode", passport.getUnitCode());
        parameters.put("dateReleased", DateEditor.formatLocalDateToString(passport.getDateReleased(), DatePattern.PATTERN_DOT));
        parameters.put("releasedBy", passport.getReleasedBy());
        parameters.put("birthPlace", passport.getBirthPlace());
        parameters.put("registration", passport.getRegistration());
        parameters.put("phone", contact.getPhone());
        parameters.put("email", contact.getEmail());
        return new JRBeanCollectionDataSource(new ArrayList<>(Collections.singletonList(parameters)));
    }

    private HashMap<String, Object> getCompanyData(String title) {
        HashMap<String, Object> parameters = new HashMap<>();
        Company company;
        try {
            company = new CompanyService().findById(1L);
        } catch (NoResultException e){
            Notification.getInstance().warning("Необходимо заполнить данные об организации!\n" +
                    "Для этого перейдите в \"Настройки\" -> \"Информация об организации\"");
            return null;
        }
        parameters.put("companyName", company.getName());
        parameters.put("companyAddress", company.getActualAddress());
        parameters.put("companyBik", company.getBIK());
        parameters.put("companyInn", company.getINN());
        parameters.put("companyKpp", company.getKPP());
        parameters.put("companyOgrn", company.getOGRN());
        parameters.put("companyTelephone", company.getContact().getPhone());
        parameters.put("companyEmail", company.getContact().getEmail());
        parameters.put("title", title);
        return parameters;
    }
}