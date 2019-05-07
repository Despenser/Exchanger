package ru.golubyatnikov.money.exchange.model.generic;


import java.math.BigInteger;
import java.util.List;


public interface GenericDAO <T>{

    void create(T obj);
    void update(T obj);
    void delete(T obj);
    List<T> findAll();
    T findById(Long id);
    BigInteger isEmptyTable();
    List<T> customQuery(String query, Class<T> clazz, Object... params);
}