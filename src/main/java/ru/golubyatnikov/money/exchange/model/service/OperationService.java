package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Operation;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import java.time.LocalDate;
import java.util.List;


public class OperationService extends GenericService<Operation> {

    public OperationService() {
        super(Operation.class);
    }

    public List<Operation> find(String text) {
        String query = "select * from operation o, client c, employee e, typeOperation t, passport p " +
                "where o.client_id = c.id " +
                "and c.passport_id = p.id " +
                "and o.employee_id = e.id " +
                "and o.typeOperation_id = t.id " +
                "and concat(o.code, o.codeIn, o.codeOut, o.sumIn, o.sumOut, o.rate, " +
                "o.date, o.time, c.surname, c.name, c.middleName, p.serial, p.number) like :text1";
        return customQuery(query, Operation.class, '%' + text + '%');
    }

    public List<Operation> todayReport(LocalDate date) {
        String query = "select * from operation o where o.date = :text1";
        return customQuery(query, Operation.class, date);
    }

    public List<Operation> operationsByCurrencyCode(String name, LocalDate from, LocalDate till){
        String query = "select * from operation o " +
                "where o.codeIn = :text1 " +
                "or o.codeOut = :text2 " +
                "and o.date between :text3 and :text4";
        return customQuery(query, Operation.class, name, name, from, till);

    }
}
