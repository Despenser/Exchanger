package ru.golubyatnikov.money.exchange.controller;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import ru.golubyatnikov.money.exchange.model.entity.Status;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;


public interface Functional {

    void add(ActionEvent event);
    void view(ActionEvent event);
    void edit(ActionEvent event);
    void delete();
    void openHandler(String title, ActionEvent event);
    void report();
    void clearSearch();
    void getAll();
    void elasticSearch();
    <T, Y extends GenericService<T>> void updateStatus(T object, Y service, TableView<T> table, Status status, String message);
    <T, Y extends GenericService<T>> void delete(T object, Y service, ObservableList<T> list, TableView<T> table);
    <T> boolean checkSelected(TableView<T> tableView, String alert);
    <T> void populateTable(TableView<T> table, ObservableList<T> list);
}