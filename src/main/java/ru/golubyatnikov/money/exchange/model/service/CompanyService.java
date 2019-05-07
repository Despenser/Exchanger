package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Company;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;


public class CompanyService extends GenericService<Company> {

    public CompanyService() {
        super(Company.class);
    }
}
