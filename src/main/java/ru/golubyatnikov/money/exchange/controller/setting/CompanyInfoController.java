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
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
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

    private ProjectInformant informant;
    private CompanyService companyService;
    private Company company;
    private Validator validator;
    private ResourceBundle resources;
    private boolean lockFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(CompanyInfoController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        validator = Validator.getInstance();
        companyService = new CompanyService();
        lockFields = false;

        Platform.runLater(() -> {
            setValidateOnPane();
            loadCompany();
            editableFields();
            showDetails();
        });

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    private void loadCompany() {
        try {
            informant.logInfo("Загрузка данных об организации");
            company = companyService.findById(1L);
        } catch (NoResultException e) {
            informant.logInfo("Данных об организации не найдены");
            company = null;
        }
    }

    @FXML
    private void saveCompanyInfo() {
        informant.logInfo("Процесс обновления информации об организации");
        if (company != null) createOrUpdateCompany("update");
        else createOrUpdateCompany("create");
    }

    private void createOrUpdateCompany(String command){
        contain();
        if (validator.checkWrongFields(gridPane)){
            if (command.equals("create")) {
                companyService.create(company);
                informant.logInfoAndShowNotificationComplete("Данные об организации успешно созданы");
                setLockFields();
            }
            else if (command.equals("update")) {
                companyService.update(company);
                informant.logInfoAndShowNotificationComplete("Данные об организации успешно сохранены");
                setLockFields();
            }
        } else {
            informant.logInfoAndShowNotificationWarning(resources.getString("fields_have_not_correct_value"));
        }
    }

    @FXML
    private void editableFields() {
        if (isLock()) setUnlockFields();
        else setLockFields();
    }

    private boolean isLock(){
        return lockFields;
    }

    private void setLockFields(){
        labelFieldsEdit.setText(resources.getString("fields_are_not_editable_unlock_for_edit"));
        labelFieldsEdit.getStyleClass().setAll(StateColor.ARCHIVE.getState());
        edit.setText(resources.getString("unlock"));
        scrollPane.setVvalue(0);
        lockFieldsOnForm(true);
        lockFields = true;
    }

    private void setUnlockFields(){
        labelFieldsEdit.setText(resources.getString("fields_are_editable"));
        labelFieldsEdit.getStyleClass().setAll(StateColor.ACTIVE.getState());
        edit.setText(resources.getString("lock"));
        scrollPane.setVvalue(0);
        lockFieldsOnForm(false);
        lockFields = false;
    }

    private void lockFieldsOnForm(boolean lock) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextInputControl) ((TextInputControl) node).setEditable(!lock);
            if (node instanceof DatePicker) {
                ((DatePicker) node).setEditable(!lock);
                node.setOnMouseClicked(e -> {
                    if (!((DatePicker) node).isEditable()) ((DatePicker) node).hide();
                });
            }
        }
    }

    private void contain() {
        informant.logInfo("Сбор данных об организации с клиентской формы");
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
        }
        else editableFields();
    }

    private void setValidateOnPane(){
        informant.logInfo("Установка валидации на поля формы");
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
        //TODO валидация телефона
        txtFieldPhone.setPromptText(resources.getString("company_phone"));
    }
}