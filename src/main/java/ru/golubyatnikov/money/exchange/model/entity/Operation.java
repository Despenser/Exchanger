package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "operation", schema = "bank")
@Access(AccessType.PROPERTY)
public class Operation {

    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private LongProperty code;

    @Column(name = "date", nullable = false)
    private ObjectProperty<LocalDate> date;

    @Column(name = "time", nullable = false)
    private ObjectProperty<LocalTime> time;

    @Column(name = "rate", nullable = false)
    private FloatProperty rate;

    @Column(name = "sumIn", nullable = false)
    private StringProperty sumIn;

    @Column(name = "sumOut", nullable = false)
    private StringProperty sumOut;

    @Column(name = "codeIn", nullable = false)
    private StringProperty codeIn;

    @Column(name = "codeOut", nullable = false)
    private StringProperty codeOut;

    private TypeOperation typeOperation;

    private Employee employee;

    private Client client;

    private Set<Currency> currencies;

    public Operation() {
        this.code = new SimpleLongProperty();
        this.date = new SimpleObjectProperty<>();
        this.time = new SimpleObjectProperty<>();
        this.rate = new SimpleFloatProperty();
        this.sumIn = new SimpleStringProperty();
        this.sumOut = new SimpleStringProperty();
        this.codeIn = new SimpleStringProperty();
        this.codeOut = new SimpleStringProperty();
        this.currencies = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code.get();
    }

    public LongProperty codeOperationProperty() {
        return code;
    }

    public void setCode(Long codeOperation) {
        this.code.set(codeOperation);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateOperationProperty() {
        return date;
    }

    public void setDate(LocalDate dateOperation) {
        this.date.set(dateOperation);
    }

    public LocalTime getTime() {
        return time.get();
    }

    public ObjectProperty<LocalTime> timeOperationProperty() {
        return time;
    }

    public void setTime(LocalTime timeOperation) {
        this.time.set(timeOperation);
    }

    public float getRate() {
        return rate.get();
    }

    public FloatProperty rateProperty() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate.set(rate);
    }

    public String getSumIn() {
        return sumIn.get();
    }

    public StringProperty sumInProperty() {
        return sumIn;
    }

    public void setSumIn(String sumIn) {
        this.sumIn.set(sumIn);
    }

    public String getSumOut() {
        return sumOut.get();
    }

    public StringProperty sumOutProperty() {
        return sumOut;
    }

    public void setSumOut(String sumOut) {
        this.sumOut.set(sumOut);
    }

    public String getCodeIn() {
        return codeIn.get();
    }

    public StringProperty codeInProperty() {
        return codeIn;
    }

    public void setCodeIn(String codeIn) {
        this.codeIn.set(codeIn);
    }

    public String getCodeOut() {
        return codeOut.get();
    }

    public StringProperty codeOutProperty() {
        return codeOut;
    }

    public void setCodeOut(String codeOut) {
        this.codeOut.set(codeOut);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeOperation_id", nullable = false)
    public TypeOperation getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(TypeOperation typeOperation) {
        this.typeOperation = typeOperation;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "operation_currency", joinColumns = @JoinColumn(name = "operation_id"), inverseJoinColumns = @JoinColumn(name = "currency_id"))
    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<Currency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return  Objects.equals(id, operation.id) &&
                Objects.equals(code, operation.code) &&
                Objects.equals(date, operation.date) &&
                Objects.equals(time, operation.time) &&
                Objects.equals(rate, operation.rate) &&
                Objects.equals(sumIn, operation.sumIn) &&
                Objects.equals(sumOut, operation.sumOut) &&
                Objects.equals(codeIn, operation.codeIn) &&
                Objects.equals(codeOut, operation.codeOut) &&
                Objects.equals(typeOperation, operation.typeOperation) &&
                Objects.equals(employee, operation.employee) &&
                Objects.equals(client, operation.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, date, time, rate, sumIn, sumOut, codeIn, codeOut, typeOperation, employee, client);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "  id=" + id +
                ", codeOperation=" + code +
                ", dateOperation=" + date +
                ", timeOperation=" + time +
                ", rate=" + rate +
                ", sumIn=" + sumIn +
                ", sumOut=" + sumOut +
                ", codeIn=" + codeIn +
                ", codeOut=" + codeOut +
                ", typeOperation=" + typeOperation +
                ", employee=" + employee +
                ", client=" + client +
                "}\n";
    }
}