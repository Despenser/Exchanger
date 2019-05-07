package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Gender;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;


public class GenderService extends GenericService<Gender> {

    public GenderService() {
        super(Gender.class);
    }
}