package ru.golubyatnikov.money.exchange.model.enumirate;


public enum DatePattern {

    PATTERN_DOT("dd.MM.yyyy"),
    PATTERN_SLASH("dd/MM/yyyy");

    private String pattern;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getValue() {
        return pattern;
    }
}
