package ru.golubyatnikov.money.exchange.model.entity;


import javafx.beans.property.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "company", schema = "bank")
@Access(AccessType.PROPERTY)
public class Company {

    private Long id;

    @Column(name = "name", nullable = false, length = 60)
    private StringProperty name;

    @Column(name = "corScore", nullable = false, length = 20)
    private StringProperty corScore;

    @Column(name = "bik", nullable = false, length = 9)
    private StringProperty BIK;

    @Column(name = "inn", nullable = false, length = 12)
    private StringProperty INN;

    @Column(name = "kpp", nullable = false, length = 9)
    private StringProperty KPP;

    @Column(name = "ogrn", nullable = false, length = 13)
    private StringProperty OGRN;

    @Column(name = "okpo", nullable = false, length = 10)
    private StringProperty OKPO;

    @Column(name = "okato", nullable = false, length = 15)
    private StringProperty OKATO;

    @Column(name = "actualAddress", nullable = false)
    private StringProperty actualAddress;

    @Column(name = "legalAddress", nullable = false)
    private StringProperty legalAddress;

    @Column(name = "licence", nullable = false, length = 10)
    private StringProperty licence;

    @Column(name = "dateRegistration", nullable = false)
    private ObjectProperty<LocalDate> dateRegistration;

    private Contact contact;

    public Company() {
        this.name = new SimpleStringProperty();
        this.corScore = new SimpleStringProperty();
        this.BIK = new SimpleStringProperty();
        this.INN = new SimpleStringProperty();
        this.KPP = new SimpleStringProperty();
        this.OGRN = new SimpleStringProperty();
        this.OKPO = new SimpleStringProperty();
        this.OKATO = new SimpleStringProperty();
        this.actualAddress = new SimpleStringProperty();
        this.legalAddress = new SimpleStringProperty();
        this.licence = new SimpleStringProperty();
        this.dateRegistration = new SimpleObjectProperty<>();
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

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCorScore() {
        return corScore.get();
    }

    public void setCorScore(String corScore) {
        this.corScore.set(corScore);
    }

    public String getBIK() {
        return BIK.get();
    }

    public void setBIK(String bik) {
        this.BIK.set(bik);
    }

    public String getINN() {
        return INN.get();
    }

    public void setINN(String inn) {
        this.INN.set(inn);
    }

    public String getKPP() {
        return KPP.get();
    }

    public void setKPP(String kpp) {
        this.KPP.set(kpp);
    }

    public String getOGRN() {
        return OGRN.get();
    }

    public void setOGRN(String ogrn) {
        this.OGRN.set(ogrn);
    }

    public String getOKPO() {
        return OKPO.get();
    }

    public void setOKPO(String okpo) {
        this.OKPO.set(okpo);
    }

    public String getOKATO() {
        return OKATO.get();
    }

    public void setOKATO(String okato) {
        this.OKATO.set(okato);
    }

    public String getActualAddress() {
        return actualAddress.get();
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress.set(actualAddress);
    }

    public String getLegalAddress() {
        return legalAddress.get();
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress.set(legalAddress);
    }

    public String getLicence() {
        return licence.get();
    }

    public void setLicence(String licence) {
        this.licence.set(licence);
    }

    public LocalDate getDateRegistration() {
        return dateRegistration.get();
    }

    public void setDateRegistration(LocalDate dateRegistration) {
        this.dateRegistration.set(dateRegistration);
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return  Objects.equals(id, company.id) &&
                Objects.equals(name, company.name) &&
                Objects.equals(corScore, company.corScore) &&
                Objects.equals(BIK, company.BIK) &&
                Objects.equals(INN, company.INN) &&
                Objects.equals(KPP, company.KPP) &&
                Objects.equals(OGRN, company.OGRN) &&
                Objects.equals(OKPO, company.OKPO) &&
                Objects.equals(OKATO, company.OKATO) &&
                Objects.equals(actualAddress, company.actualAddress) &&
                Objects.equals(legalAddress, company.legalAddress) &&
                Objects.equals(licence, company.licence) &&
                Objects.equals(dateRegistration, company.dateRegistration) &&
                Objects.equals(contact, company.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, corScore, BIK, INN, KPP, OGRN, OKPO, OKATO, actualAddress, legalAddress, licence, dateRegistration, contact);
    }

    @Override
    public String toString() {
        return "Company{" +
                "  id=" + id +
                ", name=" + name +
                ", corScore=" + corScore +
                ", BIK=" + BIK +
                ", INN=" + INN +
                ", KPP=" + KPP +
                ", OGRN=" + OGRN +
                ", OKPO=" + OKPO +
                ", OKATO=" + OKATO +
                ", actualAddress=" + actualAddress +
                ", legalAddress=" + legalAddress +
                ", licence=" + licence +
                ", dateRegistration=" + dateRegistration +
                ", contact=" + contact +
                '}';
    }
}