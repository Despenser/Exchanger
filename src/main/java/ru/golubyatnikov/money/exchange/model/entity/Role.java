package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "role", schema = "bank")
@Access(AccessType.PROPERTY)
public class Role {

    private Long id;

    @Column(name = "type", nullable = false, length = 20)
    private StringProperty type;

    private Set<Employee> employees;

    public Role() {
        this.type = new SimpleStringProperty();
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
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
        Role role = (Role) o;
        return  Objects.equals(id, role.id) &&
                Objects.equals(type, role.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", type=" + type + "}\n";
    }
}