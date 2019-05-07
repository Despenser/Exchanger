package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Currency;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import java.time.LocalDate;
import java.util.List;


public class CurrencyService extends GenericService<Currency> {

    public CurrencyService() {
        super(Currency.class);
    }

    public List<Currency> findLastDate() {
        String query = "select * from currency where currencyDate = (select max(currencyDate) from currency)";
        return customQuery(query, Currency.class);
    }

    public List<Currency> findLastButOneDate() {
        String query = "select * from (select *, row_number() over(partition by charCode order by currencyDate desc) rn from currency) A where rn = 2";
        return customQuery(query, Currency.class);
    }

    public List<Currency> currenciesForPeriod(LocalDate from, LocalDate till) {
        String query = "select * from currency where currencyDate between :text1 and :text2";
        return customQuery(query, Currency.class, from, till);
    }
}