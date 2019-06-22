package ru.golubyatnikov.money.exchange.model.util;


import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.service.*;
import java.time.LocalDate;


public class UploadWorkDataToBase {

    private ProjectInformant informant;
    private static EmployeeService employeeService;
    private static TypeOperationService typeOperationService;
    private static GenderService genderService;
    private static RoleService roleService;
    private static StatusService statusService;
    private static UploadWorkDataToBase instance;

    private UploadWorkDataToBase() {
        informant = new ProjectInformant(UploadWorkDataToBase.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        employeeService = new EmployeeService();
        typeOperationService = new TypeOperationService();
        genderService = new GenderService();
        roleService = new RoleService();
        statusService = new StatusService();

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    public static UploadWorkDataToBase getInstance() {
        if (instance == null) {
            synchronized (UploadWorkDataToBase.class) {
                if (instance == null) instance = new UploadWorkDataToBase();
            }
        }
        return instance;
    }

    public void createStatus() {
        informant.logInfo("Создание статусов, используемых в системе");

        Status active = new Status();
        active.setName("Активный");

        Status archive = new Status();
        archive.setName("В архиве");

        statusService.create(active);
        statusService.create(archive);

        informant.logInfo("Создание статусов - завершено");
    }

    public void createTypeOperations() {
        informant.logInfo("Создание типов операций, используемых в системе");

        TypeOperation buy = new TypeOperation();
        buy.setType("Покупка");

        TypeOperation sale = new TypeOperation();
        sale.setType("Продажа");

        TypeOperation cross = new TypeOperation();
        cross.setType("Кросс");

        typeOperationService.create(buy);
        typeOperationService.create(sale);
        typeOperationService.create(cross);

        informant.logInfo("Создание типов операций - завершено");
    }

    public void createGenders() {
        informant.logInfo("Создание гендерных данных, используемых в системе");

        Gender male = new Gender();
        male.setType("Мужской");

        Gender female = new Gender();
        female.setType("Женский");

        genderService.create(male);
        genderService.create(female);

        informant.logInfo("Создание гендерных данных - завершено");
    }

    public void createRoles() {
        informant.logInfo("Создание ролей, используемых в системе");

        Role admin = new Role();
        admin.setType("Администратор");

        Role user = new Role();
        user.setType("Оператор");

        Role audit = new Role();
        audit.setType("Аудитор");

        roleService.create(admin);
        roleService.create(user);
        roleService.create(audit);

        informant.logInfo("Создание ролей - завершено");
    }

    public void createAdmin() {
        informant.logInfo("Создание учетной записи администратора");
        Employee employee = new Employee();
        employee.setSurname("Администратор");
        employee.setName("");
        employee.setMiddleName("");
        employee.setBirthday(LocalDate.now());
        employee.setGender(genderService.findById(1L));

        Contact contact = new Contact();
        contact.setPhone("");
        contact.setEmail("");
        employee.setContact(contact);

        Passport passport = new Passport();
        passport.setSerial(1);
        passport.setNumber(1);
        passport.setUnitCode(1);
        passport.setBirthPlace("");
        passport.setReleasedBy("");
        passport.setDateReleased(LocalDate.now());
        passport.setRegistration("");
        employee.setPassport(passport);

        Auth admin = new Auth();
        admin.setLogin("admin");
        admin.setPassword("admin");
        employee.setAuth(admin);

        employee.setRole(roleService.findById(1L));
        employee.setStatus(statusService.findById(1L));

        employeeService.create(employee);

        informant.logInfo("Создание учетной записи администратора - завершено");
    }
}