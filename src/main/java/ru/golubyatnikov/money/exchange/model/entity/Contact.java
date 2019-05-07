package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "contact", schema = "bank")
@Access(AccessType.PROPERTY)
public class Contact {

    private Long id;

    @Column(name = "phone", nullable = false, length = 45)
    private StringProperty phone;

    @Column(name = "email", length = 45)
    private StringProperty email;

    public Contact() {
        phone = new SimpleStringProperty();
        email = new SimpleStringProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return  Objects.equals(id, contact.id) &&
                Objects.equals(phone, contact.phone) &&
                Objects.equals(email, contact.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, email);
    }

    @Override
    public String toString() {
        return "Contact{" + "id=" + id + ", phone=" + phone + ", email=" + email + "}\n";
    }
}