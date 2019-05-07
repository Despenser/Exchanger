package ru.golubyatnikov.money.exchange.controller;


import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;


public interface ModalFunctional {

    void checkMode();
    void useButtonAction(ActionEvent event);
    Object create(ActionEvent event);
    void update(ActionEvent event);
    void report();
    Object contain();
    void showDetails();
    void setValidationOnPane();
    boolean validate(GridPane... gridPanes);
    void closeWindow(ActionEvent event);
    void lockFields(boolean lock, GridPane... gridPanes);
}