package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "passport", schema = "bank")
@Access(AccessType.PROPERTY)
public class Passport {

    private Long id;

    @Column(name = "serial", nullable = false, length = 4)
    private LongProperty serial;

    @Column(name = "number", nullable = false, length = 6)
    private LongProperty number;

    @Column(name = "unitCode", nullable = false, length = 7)
    private IntegerProperty unitCode;

    @Column(name = "dateReleased", nullable = false)
    private ObjectProperty<LocalDate> dateReleased;

    @Column(name = "releasedBy", nullable = false)
    private StringProperty releasedBy;

    @Column(name = "birthPlace", nullable = false)
    private StringProperty birthPlace;

    @Column(name = "registration", nullable = false)
    private StringProperty registration;


    public Passport() {
        this.serial = new SimpleLongProperty();
        this.number = new SimpleLongProperty();
        this.unitCode = new SimpleIntegerProperty();
        this.dateReleased = new SimpleObjectProperty<>();
        this.releasedBy = new SimpleStringProperty();
        this.birthPlace = new SimpleStringProperty();
        this.registration = new SimpleStringProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSerial() {
        return serial.get();
    }

    public LongProperty serialProperty() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial.set(serial);
    }

    public long getNumber() {
        return number.get();
    }

    public LongProperty numberProperty() {
        return number;
    }

    public void setNumber(long number) {
        this.number.set(number);
    }

    public int getUnitCode() {
        return unitCode.get();
    }

    public void setUnitCode(int unitCode) {
        this.unitCode.set(unitCode);
    }

    public LocalDate getDateReleased() {
        return dateReleased.get();
    }

    public void setDateReleased(LocalDate dateReleased) {
        this.dateReleased.set(dateReleased);
    }

    public String getReleasedBy() {
        return releasedBy.get();
    }

    public void setReleasedBy(String releasedBy) {
        this.releasedBy.set(releasedBy);
    }

    public String getBirthPlace() {
        return birthPlace.get();
    }

    public StringProperty birthPlaceProperty() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace.set(birthPlace);
    }

    public String getRegistration() {
        return registration.get();
    }

    public void setRegistration(String registration) {
        this.registration.set(registration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passport passport = (Passport) o;
        return  Objects.equals(id, passport.id) &&
                Objects.equals(serial, passport.serial) &&
                Objects.equals(number, passport.number) &&
                Objects.equals(unitCode, passport.unitCode) &&
                Objects.equals(dateReleased, passport.dateReleased) &&
                Objects.equals(releasedBy, passport.releasedBy) &&
                Objects.equals(birthPlace, passport.birthPlace) &&
                Objects.equals(registration, passport.registration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serial, number, unitCode, dateReleased, releasedBy, birthPlace, registration);
    }

    @Override
    public String toString() {
        return  "Passport{" +
                "  id=" + id +
                ", serial=" + serial +
                ", number=" + number +
                ", unitCode=" + unitCode +
                ", dateReleased=" + dateReleased +
                ", releasedBy=" + releasedBy +
                ", birthPlace=" + birthPlace +
                ", registration=" + registration +
                "}\n";
    }
}