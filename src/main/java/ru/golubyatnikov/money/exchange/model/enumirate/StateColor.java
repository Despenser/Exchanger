package ru.golubyatnikov.money.exchange.model.enumirate;


public enum StateColor {

    NEW("blue-text"),
    ACTIVE("green-text"),
    ARCHIVE("red-text");

    private String state;

    StateColor (String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}