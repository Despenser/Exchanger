package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "auth", schema = "bank")
@Access(AccessType.PROPERTY)
public class Auth {

    private Long id;

    @Column(name = "login", nullable = false, length = 45)
    private StringProperty login;

    @Column(name = "password", nullable = false, length = 45)
    private StringProperty password;

    public Auth() {
        this.login = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auth auth = (Auth) o;
        return  Objects.equals(id, auth.id) &&
                Objects.equals(login, auth.login) &&
                Objects.equals(password, auth.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password);
    }

    @Override
    public String toString() {
        return "Auth{" + "id=" + id + ", login=" + login + ", password=" + password + "}\n";
    }
}