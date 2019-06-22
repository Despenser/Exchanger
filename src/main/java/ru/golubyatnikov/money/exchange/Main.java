package ru.golubyatnikov.money.exchange;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.jdom2.JDOMException;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import ru.golubyatnikov.money.exchange.model.service.*;
import ru.golubyatnikov.money.exchange.model.util.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ResourceBundle;


public class Main extends Application {

    private ObservableList<Client> clients;
    private ObservableList<Employee> employees;
    private ObservableList<TypeOperation> typeOperations;
    private ObservableList<Status> statuses;
    private ObservableList<Gender> genders;
    private ObservableList<Role> roles;

    private ProjectInformant informant;
    private UploadWorkDataToBase uploadWorkDataToBase;
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

    public boolean isNotAdmin() {
        return !isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public void init() {
        informant = new ProjectInformant(Main.class);

        Platform.runLater(Hibernate::getSessionFactory);
        uploadWorkDataToBase = UploadWorkDataToBase.getInstance();

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
        informant.logInfo("Загрузка актуальных курсов валют");
        try {
            ActualCurrency.getInstance().uploadForTodayOrMonth();
            informant.logInfo("Загрузка актуальных курсов валют завершена");
        } catch (IOException | JDOMException e) {
            informant.logError("При загрузке курсов валют произошел сбой", e);
        }
    }

    private <T extends GenericService> void checkDataInTable(T t, ObservableList<?> list, String commandCreate) {
        informant.logInfo("Проверка данных в базе для работы");
        if (t.isEmptyTable().equals(new BigInteger("1"))) {
            informant.logInfo("Данные найдены, процесс наполнения коллекции");
            list.setAll(t.findAll());
        }
        else {
            informant.logInfo("Данные в таблице не найдены, процедура наполнения таблицы данными...");
            setDataInTable(commandCreate);
            informant.logInfo("Наполнение таблицы - завершено, процесс наполнения коллекции данными");
            list.setAll(t.findAll());
        }
    }

    private void setDataInTable(String whatPopulate) {
        switch (whatPopulate) {
            case "createRoles":
                informant.logInfo("Запущен процесс создания ролей");
                uploadWorkDataToBase.createRoles();
                break;
            case "createGenders":
                informant.logInfo("Запущен процесс создания статусов половой принадлежности");
                uploadWorkDataToBase.createGenders();
                break;
            case "createStatuses":
                informant.logInfo("Запущен процесс создания статусов");
                uploadWorkDataToBase.createStatus();
                break;
            case "createTypeOperation":
                informant.logInfo("Запущен процесс создания типов операций");
                uploadWorkDataToBase.createTypeOperations();
                break;
            case "createAdmin":
                informant.logInfo("Запущен процесс создания учетной записи администратора");
                uploadWorkDataToBase.createAdmin();
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
        informant.logInfo("Процесс запуска окна авторизации");
        LoaderFXML.getInstance().loadRoot(stage, "login/loginPane",
                ResourceBundle.getBundle("bundle/localization").getString("application_name"), 419, 367, false);
        LoaderFXML.getInstance().setMain(this);
        informant.logInfo("Окно авторизации запущено");
    }

    @Override
    public void stop() {
        informant.logInfo("Завершение работы приложения");
        Hibernate.shutdown();
        Platform.exit();
    }
}