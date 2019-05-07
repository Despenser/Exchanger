package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;


@Entity
@Table(name = "client", schema = "bank")
@Access(AccessType.PROPERTY)
public class Client {

    private Long id;

    @Column(name = "surname", nullable = false, length = 45)
    private StringProperty surname;

    @Column(name = "name", nullable = false, length = 45)
    private StringProperty name;

    @Column(name = "middleName", length = 45)
    private StringProperty middleName;

    @Column(name = "birthday", nullable = false)
    private ObjectProperty<LocalDate> birthday;

    private Status status;

    private Gender gender;

    private Passport passport;

    private Contact contact;

    private Set<Operation> operations;

    public Client() {
        this.surname = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.middleName = new SimpleStringProperty();
        this.birthday = new SimpleObjectProperty<>();
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

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
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

    public String getMiddleName() {
        return middleName.get();
    }

    public StringProperty middleNameProperty() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName.set(middleName);
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
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
        Client client = (Client) o;
        return  Objects.equals(id, client.id) &&
                Objects.equals(surname, client.surname) &&
                Objects.equals(name, client.name) &&
                Objects.equals(middleName, client.middleName) &&
                Objects.equals(birthday, client.birthday) &&
                Objects.equals(gender, client.gender) &&
                Objects.equals(passport, client.passport) &&
                Objects.equals(contact, client.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, surname, name, middleName, birthday, gender, passport, contact);
    }

    @Override
    public String toString() {
        return  "Client{" +
                "  id=" + id +
                ", surname=" + surname +
                ", name=" + name +
                ", middleName=" + middleName +
                ", birthday=" + birthday +
                ", gender=" + gender +
                ", passport=" + passport +
                ", contact=" + contact +
                "}\n";
    }
}