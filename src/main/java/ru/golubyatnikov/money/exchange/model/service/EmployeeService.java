package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import javax.persistence.NoResultException;
import java.util.List;


public class EmployeeService extends GenericService<Employee> {

    public EmployeeService() {
        super(Employee.class);
    }

    public Employee findByLogin(String login) throws NoResultException {
        String query =  "select * from employee e, auth a, status s " +
                "where e.auth_id = a.id " +
                "and e.status_id = s.id " +
                "and s.id != 2 " +
                "and a.login = :text1";
        List <Employee> employees = customQuery(query, Employee.class, login);
        return !employees.isEmpty() ? employees.get(0) : null;
    }

    public Employee findByLoginWithoutStatus(String login) throws NoResultException {
        String query =  "select * from employee e, auth a " +
                "where e.auth_id = a.id " +
                "and a.login = :text1";
        List <Employee> employees = customQuery(query, Employee.class, login);
        return !employees.isEmpty() ? employees.get(0) : null;
    }

    public Employee findByIdAndLoginWithoutStatus(long id, String login) throws NoResultException {
        String query =  "select * from employee e, auth a " +
                "where e.auth_id = a.id " +
                "and e.id != :text1 " +
                "and a.login = :text2";
        List <Employee> employees = customQuery(query, Employee.class, id, login);
        return !employees.isEmpty() ? employees.get(0) : null;
    }

    public List<Employee> findEmployeePane(String text) {
        String query =  "select * from employee e, passport p, contact c " +
                "where e.passport_id = p.id " +
                "and e.contact_id = c.id " +
                "and concat(surname, name, middleName, serial, number, birthPlace, phone, email) like :text1";
        return customQuery(query, Employee.class, '%' + text + '%');
    }

    public Employee findBySerialNumber(long serial, long number) {
        String query = "select * from employee e, passport p where  e.passport_id = p.id and p.serial = :text1 and p.number = :text2";
        List<Employee> employees = customQuery(query, Employee.class, serial, number);
        return !employees.isEmpty() ? employees.get(0) : null;
    }

    public Employee findByIdAndSerialNumber(long id, long serial, long number) {
        String query = "select * from employee e, passport p where e.passport_id = p.id and e.id != :text1 and p.serial = :text2 and p.number = :text3";
        List<Employee> employees = customQuery(query, Employee.class, id, serial, number);
        return !employees.isEmpty() ? employees.get(0) : null;
    }
}