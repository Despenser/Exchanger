package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "status", schema = "bank")
@Access(AccessType.PROPERTY)
public class Status {

    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private StringProperty name;

    private Set<Client> clients;

    private Set<Employee> employees;

    public Status() {
        this.name = new SimpleStringProperty();
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
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
        Status status = (Status) o;
        return  Objects.equals(id, status.id) &&
                Objects.equals(name, status.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Status{" + "id=" + id + ", name=" + name + "}\n";
    }
}