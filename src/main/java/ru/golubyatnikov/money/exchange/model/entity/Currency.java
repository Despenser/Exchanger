package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "currency", schema = "bank")
@Access(AccessType.PROPERTY)
public class Currency {

    private Long id;

    @Column(name = "currencyDate", nullable = false)
    private ObjectProperty<LocalDate> currencyDate;

    @Column(name = "numCode", nullable = false, length = 3)
    private IntegerProperty numCode;

    @Column(name = "charCode", nullable = false, length = 3)
    private StringProperty charCode;

    @Column(name = "nominal", nullable = false, length = 6)
    private IntegerProperty nominal;

    @Column(name = "name", nullable = false, length = 65)
    private StringProperty name;

    @Column(name = "value", nullable = false, length = 10)
    private FloatProperty value;

    @Column(name = "valueSale", nullable = false)
    private FloatProperty valueSale;

    @Column(name = "valueBuy", nullable = false)
    private FloatProperty valueBuy;

    private Set<Operation> operations;

    public Currency() {
        this.currencyDate = new SimpleObjectProperty<>();
        this.numCode = new SimpleIntegerProperty();
        this.charCode = new SimpleStringProperty();
        this.nominal = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.value = new SimpleFloatProperty();
        this.valueSale = new SimpleFloatProperty();
        this.valueBuy = new SimpleFloatProperty();
        this.operations = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCurrencyDate() {
        return currencyDate.get();
    }

    public ObjectProperty<LocalDate> currencyDateProperty() {
        return currencyDate;
    }

    public void setCurrencyDate(LocalDate currencyDate) {
        this.currencyDate.set(currencyDate);
    }

    public int getNumCode() {
        return numCode.get();
    }

    public IntegerProperty numCodeProperty() {
        return numCode;
    }

    public void setNumCode(int numCode) {
        this.numCode.set(numCode);
    }

    public String getCharCode() {
        return charCode.get();
    }

    public StringProperty charCodeProperty() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode.set(charCode);
    }

    public int getNominal() {
        return nominal.get();
    }

    public IntegerProperty nominalProperty() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal.set(nominal);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public float getValue() {
        return value.get();
    }

    public FloatProperty valueProperty() {
        return value;
    }

    public void setValue(float value) {
        this.value.set(value);
    }

    public float getValueSale() {
        return valueSale.get();
    }

    public FloatProperty valueSaleProperty() {
        return valueSale;
    }

    public void setValueSale(float valueSale) {
        this.valueSale.set(valueSale);
    }

    public float getValueBuy() {
        return valueBuy.get();
    }

    public FloatProperty valueBuyProperty() {
        return valueBuy;
    }

    public void setValueBuy(float valueBuy) {
        this.valueBuy.set(valueBuy);
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "currencies")
    public Set<Operation> getOperations() {
        return operations;
    }

    public void setOperations(Set<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return  Objects.equals(id, currency.id) &&
                Objects.equals(currencyDate, currency.currencyDate) &&
                Objects.equals(numCode, currency.numCode) &&
                Objects.equals(charCode, currency.charCode) &&
                Objects.equals(nominal, currency.nominal) &&
                Objects.equals(name, currency.name) &&
                Objects.equals(value, currency.value) &&
                Objects.equals(valueSale, currency.valueSale) &&
                Objects.equals(valueBuy, currency.valueBuy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyDate, numCode, charCode, nominal, name, value, valueSale, valueBuy);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "  id=" + id +
                ", currencyDate=" + currencyDate +
                ", numCode=" + numCode +
                ", charCode=" + charCode +
                ", nominal=" + nominal +
                ", name=" + name +
                ", value=" + value +
                ", valueSale=" + valueSale +
                ", valueBuy=" + valueBuy +
                "}\n";
    }
}