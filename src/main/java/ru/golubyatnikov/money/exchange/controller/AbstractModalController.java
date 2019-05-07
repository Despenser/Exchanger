package ru.golubyatnikov.money.exchange.controller;


import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.golubyatnikov.money.exchange.model.enumirate.Mode;
import ru.golubyatnikov.money.exchange.model.util.Validator;


public abstract class AbstractModalController implements ModalFunctional {

    @Override
    public abstract void checkMode();

    @Override
    public abstract void useButtonAction(ActionEvent event);

    @Override
    public abstract Object create(ActionEvent event);

    @Override
    public abstract void update(ActionEvent event);

    @Override
    public abstract void report();

    @Override
    public abstract Object contain();

    @Override
    public abstract void showDetails();

    @Override
    public abstract void setValidationOnPane();

    protected void action(ActionEvent event, Mode mode){
        if (mode == Mode.CREATE) create(event);
        else if (mode == Mode.EDIT) update(event);
        else if (mode == Mode.VIEW) report();
    }

    @Override
    public boolean validate(GridPane... gridPanes) {
        return Validator.getInstance().checkWrongFields(gridPanes);
    }

    @Override
    public void closeWindow(ActionEvent event) {
        ((Stage) ((Node)event.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void lockFields(boolean lock, GridPane... gridPanes) {
        for (GridPane pane : gridPanes) {
            for (Node node : pane.getChildren()) {
                if (node instanceof TextInputControl) ((TextInputControl) node).setEditable(!lock);
                if (node instanceof ComboBoxBase<?>) {
                    ((ComboBoxBase<?>) node).setEditable(!lock);
                    node.setOnMouseClicked(e -> {
                        if (!((ComboBoxBase<?>) node).isEditable()) ((ComboBoxBase<?>) node).hide();
                    });
                }
            }
        }
    }
}