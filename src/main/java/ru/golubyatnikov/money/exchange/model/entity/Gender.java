package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "gender", schema = "bank")
@Access(AccessType.PROPERTY)
public class Gender {

    private Long id;

    @Column(name = "type", nullable = false, length = 10)
    private StringProperty type;

    private Set<Client> clients;

    private Set<Employee> employees;

    public Gender() {
        this.type = new SimpleStringProperty();
        this.clients = new HashSet<>();
        this.employees = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gender")
    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gender")
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gender gender = (Gender) o;
        return  Objects.equals(id, gender.id) &&
                Objects.equals(type, gender.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return "Gender{" + "id=" + id + ", type=" + type + "}\n";
    }
}