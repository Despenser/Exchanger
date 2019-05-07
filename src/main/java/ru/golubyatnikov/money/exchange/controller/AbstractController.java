package ru.golubyatnikov.money.exchange.controller;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import ru.golubyatnikov.money.exchange.model.entity.Client;
import ru.golubyatnikov.money.exchange.model.entity.Employee;
import ru.golubyatnikov.money.exchange.model.entity.Status;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import ru.golubyatnikov.money.exchange.model.util.Notification;


public abstract class AbstractController implements Functional {

    @Override
    public abstract void add(ActionEvent event);

    @Override
    public abstract void view(ActionEvent event);

    @Override
    public abstract void edit(ActionEvent event);

    @Override
    public abstract void delete();

    @Override
    public abstract void openHandler(String title, ActionEvent event);

    @Override
    public abstract void report();

    @Override
    public abstract void clearSearch();

    @Override
    public abstract void getAll();

    @Override
    public abstract void elasticSearch();

    @Override
    public <T, Y extends GenericService<T>> void updateStatus(T object, Y service, TableView<T> table, Status status, String message) {
        if (Notification.getInstance().confirmation(message)) {
            if (object instanceof Employee) ((Employee) object).setStatus(status);
            else if (object instanceof Client) ((Client) object).setStatus(status);
            service.update(object);
            table.refresh();
        }
    }

    @Override
    public <T, Y extends GenericService<T>> void delete(T object, Y service, ObservableList<T> list, TableView<T> table) {
        service.delete(object);
        list.remove(object);
        table.refresh();
    }

    @Override
    public <T> boolean checkSelected(TableView<T> tableView, String alert) {
        if (!tableView.getSelectionModel().isEmpty()) return true;
        else {
            Notification.getInstance().info(alert);
            return false;
        }
    }

    @Override
    public <T> void populateTable(TableView<T> table, ObservableList<T> list) {
        table.setItems(list);
    }
}