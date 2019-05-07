package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.TypeOperation;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;


public class TypeOperationService extends GenericService<TypeOperation> {

    public TypeOperationService() {
        super(TypeOperation.class);
    }
}
