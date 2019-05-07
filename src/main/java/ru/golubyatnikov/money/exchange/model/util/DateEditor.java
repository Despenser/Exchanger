package ru.golubyatnikov.money.exchange.model.util;


import ru.golubyatnikov.money.exchange.model.enumirate.DatePattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DateEditor {

    public static String formatLocalDateToString(LocalDate date, DatePattern pattern) {
        if (date == null) return null;
        return DateTimeFormatter.ofPattern(pattern.getValue()).format(date);
    }

    public static LocalDate parseToLocalDate(String dateString, DatePattern pattern) {
        return DateTimeFormatter.ofPattern(pattern.getValue()).parse(dateString, LocalDate::from);
    }
}