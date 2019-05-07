package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "typeOperation", schema = "bank")
@Access(AccessType.PROPERTY)
public class TypeOperation {

    private Long id;

    @Column(name = "type", nullable = false, length = 10)
    private StringProperty type;

    private Set<Operation> operations;

    public TypeOperation() {
        this.type = new SimpleStringProperty();
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

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "typeOperation")
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
        TypeOperation typeOperation = (TypeOperation) o;
        return  Objects.equals(id, typeOperation.id) &&
                Objects.equals(type, typeOperation.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return "TypeOperation{" + "id=" + id + ", type=" + type + "}\n";
    }
}