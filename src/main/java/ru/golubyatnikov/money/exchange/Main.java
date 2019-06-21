package ru.golubyatnikov.money.exchange;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import ru.golubyatnikov.money.exchange.model.service.*;
import ru.golubyatnikov.money.exchange.model.util.ActualCurrency;
import ru.golubyatnikov.money.exchange.model.util.AddDataToBase;
import ru.golubyatnikov.money.exchange.model.util.Hibernate;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ResourceBundle;


/*
  TODO Список основных задач
* Логирование действий
* Валидация телефона и валидация формы заведения клиента
* Внести в бандлы использование строк за исключением логирования
* Логирование сделать на английском
* Рефакторинг проекта
*/
public class Main extends Application {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private ObservableList<Client> clients;
    private ObservableList<Employee> employees;
    private ObservableList<TypeOperation> typeOperations;
    private ObservableList<Status> statuses;
    private ObservableList<Gender> genders;
    private ObservableList<Role> roles;

    private AddDataToBase addDataToBase;
    private Employee currentEmployee;
    private volatile boolean isAdmin;

    public ObservableList<Client> getClients() {
        return clients;
    }

    public void setClients(ObservableList<Client> clients) {
        this.clients = clients;
    }

    public ObservableList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ObservableList<Employee> employees) {
        this.employees = employees;
    }

    public ObservableList<TypeOperation> getTypeOperations() {
        return typeOperations;
    }

    public ObservableList<Status> getStatuses() {
        return statuses;
    }

    public ObservableList<Gender> getGenders() {
        return genders;
    }

    public ObservableList<Role> getRoles() {
        return roles;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public boolean isAdmin() {
        return !isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }


    @Override
    public void init() {
        Platform.runLater(Hibernate::getSessionFactory);
        addDataToBase = AddDataToBase.getInstance();

        statuses = FXCollections.observableArrayList();
        roles = FXCollections.observableArrayList();
        genders = FXCollections.observableArrayList();
        typeOperations = FXCollections.observableArrayList();
        employees = FXCollections.observableArrayList();
        clients = FXCollections.observableArrayList();

        uploadCurrencies();

        checkDataInTable(new StatusService(), statuses, "createStatuses");
        checkDataInTable(new RoleService(), roles, "createRoles");
        checkDataInTable(new GenderService(), genders, "createGenders");
        checkDataInTable(new TypeOperationService(), typeOperations,"createTypeOperation");
        checkDataInTable(new EmployeeService(), employees, "createAdmin");
        checkDataInTable(new ClientService(), clients, "");
    }

    private void uploadCurrencies(){
        try {
            ActualCurrency.getInstance().uploadForTodayOrMonth();
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }

    private <T extends GenericService> void checkDataInTable(T t, ObservableList<?> list, String commandCreate) {
        if (t.isEmptyTable().equals(new BigInteger("1"))) list.setAll(t.findAll());
        else {
            setDataInTable(commandCreate);
            list.setAll(t.findAll());
        }
    }

    private void setDataInTable(String whatPopulate) {
        switch (whatPopulate) {
            case "createRoles": addDataToBase.createRoles();
                break;
            case "createGenders": addDataToBase.createGenders();
                break;
            case "createStatuses": addDataToBase.createStatus();
                break;
            case "createTypeOperation": addDataToBase.createTypeOperations();
                break;
            case "createAdmin": addDataToBase.createAdmin();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        LoaderFXML.getInstance().loadRoot(stage, "login/loginPane", ResourceBundle.getBundle("bundle/localization").getString("application_name"),
                419, 367, false);
        LoaderFXML.getInstance().setMain(this);
    }

    @Override
    public void stop() {
        Hibernate.shutdown();
    }
}