package ru.golubyatnikov.money.exchange.model.util;


import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.service.*;
import java.time.LocalDate;


public class AddDataToBase {

    private static EmployeeService employeeService;
    private static TypeOperationService typeOperationService;
    private static GenderService genderService;
    private static RoleService roleService;
    private static StatusService statusService;
    private static AddDataToBase instance;

    private AddDataToBase() {
        employeeService = new EmployeeService();
        typeOperationService = new TypeOperationService();
        genderService = new GenderService();
        roleService = new RoleService();
        statusService = new StatusService();
    }

    public static AddDataToBase getInstance() {
        if (instance == null) {
            synchronized (AddDataToBase.class) {
                if (instance == null) instance = new AddDataToBase();
            }
        }
        return instance;
    }

    public void createStatus() {
        Status active = new Status();
        active.setName("Активный");

        Status archive = new Status();
        archive.setName("В архиве");

        statusService.create(active);
        statusService.create(archive);
    }

    public void createTypeOperations() {
        TypeOperation buy = new TypeOperation();
        buy.setType("Покупка");

        TypeOperation sale = new TypeOperation();
        sale.setType("Продажа");

        TypeOperation cross = new TypeOperation();
        cross.setType("Кросс");

        typeOperationService.create(buy);
        typeOperationService.create(sale);
        typeOperationService.create(cross);
    }

    public void createGenders() {
        Gender male = new Gender();
        male.setType("Мужской");

        Gender female = new Gender();
        female.setType("Женский");

        genderService.create(male);
        genderService.create(female);
    }

    public void createRoles() {
        Role admin = new Role();
        admin.setType("Администратор");

        Role user = new Role();
        user.setType("Оператор");

        Role audit = new Role();
        audit.setType("Аудитор");

        roleService.create(admin);
        roleService.create(user);
        roleService.create(audit);
    }

    public void createAdmin() {
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
    }
}