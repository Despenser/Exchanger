package ru.golubyatnikov.money.exchange.model.util;


import ru.golubyatnikov.money.exchange.model.entity.Currency;


public class Exchanger {


    private static volatile Exchanger instance;

    private Exchanger() {
    }

    public static Exchanger getInstance() {
        if (instance == null) {
            synchronized (Exchanger.class) {
                if (instance == null) instance = new Exchanger();
            }
        }
        return instance;
    }

    public float sale(Currency currency, float quantity) {
        return (quantity / currency.getValueSale()) * currency.getNominal();
    }

    public float buy(Currency currency, float quantity){
        return (currency.getValueBuy() * quantity) / currency.getNominal();
    }

    public float cross(Currency first, Currency second, float quantity) {
        return (first.getValueBuy() / first.getNominal()) / (second.getValueSale() / second.getNominal()) * quantity;
    }
}