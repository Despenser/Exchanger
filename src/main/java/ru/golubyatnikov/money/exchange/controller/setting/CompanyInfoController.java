package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.golubyatnikov.money.exchange.model.entity.Company;
import ru.golubyatnikov.money.exchange.model.entity.Contact;
import ru.golubyatnikov.money.exchange.model.enumirate.StateColor;
import ru.golubyatnikov.money.exchange.model.service.CompanyService;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.Validator;
import javax.persistence.NoResultException;
import java.net.URL;
import java.util.ResourceBundle;


public class CompanyInfoController implements Initializable {

    @FXML private Button edit, save;
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;
    @FXML private Label labelFieldsEdit;
    @FXML private TextArea txtAreaLegalAddress, txtAreaActualAddress;
    @FXML private DatePicker datePickerRegistration;
    @FXML private TextField txtFieldCompanyName, txtFieldLicence, txtFieldCorScore, txtFieldBIK, txtFieldINN,
            txtFieldOGRN, txtFieldOKATO, txtFieldOKPO, txtFieldKPP, txtFieldEmail, txtFieldPhone;

    private CompanyService companyService;
    private Company company;
    private Validator validator;
    private Notification notification;
    private ResourceBundle resources;
    private boolean lockFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        validator = Validator.getInstance();
        notification = Notification.getInstance();
        companyService = new CompanyService();
        lockFields = false;

        Platform.runLater(this::setValidateOnPane);
        Platform.runLater(this::loadCompany);
        Platform.runLater(this::editableFields);
        Platform.runLater(this::showDetails);
    }

    private void loadCompany() {
        try {
            company = companyService.findById(1L);
        } catch (NoResultException e) {
            company = null;
        }
    }

    @FXML
    private void editableFields() {
        if (lockFields) {
            labelFieldsEdit.setText(resources.getString("fields_are_editable"));
            labelFieldsEdit.getStyleClass().setAll(StateColor.ACTIVE.getState());
            scrollPane.setVvalue(0);
            setLockFields(true);
            lockFields = false;
            edit.setText(resources.getString("lock"));
        } else {
            labelFieldsEdit.setText(resources.getString("fields_are_not_editable_unlock_for_edit"));
            labelFieldsEdit.getStyleClass().setAll(StateColor.ARCHIVE.getState());
            scrollPane.setVvalue(0);
            setLockFields(false);
            lockFields = true;
            edit.setText(resources.getString("unlock"));
        }
    }

    @FXML
    private void saveCompanyInfo() {
        if (company != null) {
            contain();
            if (validator.checkWrongFields(gridPane)){
                companyService.update(company);
                editableFields();
            } else notification.warning(resources.getString("fields_have_not_correct_value"));
        } else {
            contain();
            if (validator.checkWrongFields(gridPane)) {
                companyService.create(company);
                editableFields();
            } else notification.warning(resources.getString("fields_have_not_correct_value"));
        }
    }

    private void contain() {
        if (company == null) company = new Company();
        company.setName(txtFieldCompanyName.getText());
        company.setLicence(txtFieldLicence.getText());
        company.setDateRegistration(datePickerRegistration.getValue());
        company.setCorScore(txtFieldCorScore.getText());
        company.setBIK(txtFieldBIK.getText());
        company.setINN(txtFieldINN.getText());
        company.setKPP(txtFieldKPP.getText());
        company.setOGRN(txtFieldOGRN.getText());
        company.setOKATO(txtFieldOKATO.getText());
        company.setOKPO(txtFieldOKPO.getText());
        company.setLegalAddress(txtAreaLegalAddress.getText());
        company.setActualAddress(txtAreaActualAddress.getText());

        Contact contact = new Contact();
        contact.setPhone(txtFieldPhone.getText());
        contact.setEmail(txtFieldEmail.getText());
        company.setContact(contact);
    }

    private void setLockFields(boolean lock) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextInputControl) ((TextInputControl) node).setEditable(lock);
            if (node instanceof DatePicker) {
                ((DatePicker) node).setEditable(lock);
                node.setOnMouseClicked(e -> {
                    if (!((DatePicker) node).isEditable()) ((DatePicker) node).hide();
                });
            }
        }
    }

    private void showDetails() {
        if (company != null) {
            txtFieldCompanyName.setText(company.getName());
            txtFieldLicence.setText(company.getLicence());
            datePickerRegistration.setValue(company.getDateRegistration());
            txtFieldCorScore.setText(company.getCorScore());
            txtFieldBIK.setText(company.getBIK());
            txtFieldINN.setText(company.getINN());
            txtFieldKPP.setText(company.getKPP());
            txtFieldOGRN.setText(company.getOGRN());
            txtFieldOKATO.setText(company.getOKATO());
            txtFieldOKPO.setText(company.getOKPO());
            txtAreaLegalAddress.setText(company.getLegalAddress());
            txtAreaActualAddress.setText(company.getActualAddress());
            txtFieldPhone.setText(company.getContact().getPhone());
            txtFieldEmail.setText(company.getContact().getEmail());
        } else editableFields();
    }

    //TODO валидация телефона
    private void setValidateOnPane(){
        validator.validatePane(save, gridPane);
        validator.fieldsNotNull(resources.getString("credit_organization"), 4, txtFieldCompanyName);
        validator.cyrillicAndInteger(resources.getString("prompt_registration"), txtAreaLegalAddress);
        validator.cyrillicAndInteger(resources.getString("prompt_registration"), txtAreaActualAddress);
        validator.datePickerNotNull(resources.getString("prompt_date_released"), datePickerRegistration);
        validator.licenceBank(resources.getString("bank_licence"), txtFieldLicence);
        validator.onlyInteger(resources.getString("bank_score"), txtFieldCorScore, 20);
        validator.onlyInteger(resources.getString("bik_num"), txtFieldBIK, 9);
        validator.onlyInteger(resources.getString("inn_num"), txtFieldINN, 10);
        validator.onlyInteger(resources.getString("ogrn_num"), txtFieldOGRN, 13);
        validator.okatoBank(resources.getString("okato_num"), txtFieldOKATO);
        validator.onlyInteger(resources.getString("okpo_num"), txtFieldOKPO, 8);
        validator.onlyInteger(resources.getString("kpp_num"), txtFieldKPP, 9);
        validator.email(resources.getString("company_email"), txtFieldEmail);
        //txtFieldPhone
    }
}