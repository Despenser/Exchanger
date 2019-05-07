package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Status;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;


public class StatusService extends GenericService<Status> {

    public StatusService() {
        super(Status.class);
    }
}
