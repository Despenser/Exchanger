package ru.golubyatnikov.money.exchange.model;


import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import ru.golubyatnikov.money.exchange.model.service.*;
import ru.golubyatnikov.money.exchange.model.util.DateEditor;
import ru.golubyatnikov.money.exchange.model.util.Hibernate;


public class AddFakeData {

    private static CompanyService companyService = new CompanyService();

    public static void main(String[] args) {
        createCompany();
    }

    private static void createCompany(){
        Company company = new Company();
        company.setName("Сбербанк");
        company.setCorScore("30101810400000000225");
        company.setLicence("1481");
        company.setDateRegistration(DateEditor.parseToLocalDate("11.08.2015", DatePattern.PATTERN_DOT));
        company.setBIK("044525225");
        company.setINN("7707083893");
        company.setKPP("773601001");
        company.setOGRN("1027700132195");
        company.setOKPO("00032537");
        company.setOKATO("45293554000");
        company.setActualAddress("Россия, 115035, Москва, ул. Балчуг, д. 2");
        company.setLegalAddress("Россия, 117997, Москва,  ул. Вавилова, д. 19");

        Contact contact = new Contact();
        contact.setEmail("sberbank@sberbank.ru");
        contact.setPhone("+74959502190");
        company.setContact(contact);

        companyService.create(company);
        Hibernate.shutdown();
    }
}