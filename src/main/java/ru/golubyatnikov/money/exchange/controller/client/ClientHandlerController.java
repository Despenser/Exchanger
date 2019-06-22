package ru.golubyatnikov.money.exchange.controller.client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.golubyatnikov.money.exchange.controller.AbstractModalController;
import ru.golubyatnikov.money.exchange.controller.operation.OperationHandlerController;
import ru.golubyatnikov.money.exchange.model.entity.*;
import ru.golubyatnikov.money.exchange.model.enumirate.Mode;
import ru.golubyatnikov.money.exchange.model.enumirate.StateColor;
import ru.golubyatnikov.money.exchange.model.service.ClientService;
import ru.golubyatnikov.money.exchange.model.util.*;
import java.net.URL;
import java.util.ResourceBundle;


public class ClientHandlerController extends AbstractModalController implements Initializable {

    @FXML private Button btnAction;
    @FXML private Label labelTitle, labelStatus;
    @FXML private GridPane gridPaneFirst, gridPaneSecond;
    @FXML private ComboBox<String> comboBoxGender;
    @FXML private DatePicker datePickerBirthday, datePickerReleased;
    @FXML private TextArea txtAreaBirthPlace, txtAreaRegistration, txtAreaReleasedBy;
    @FXML private TextField txtFieldSurname, txtFieldName, txtFieldMiddleName, txtFieldSerialPass,
            txtFieldNumPass, txtFieldUnitCode, txtFieldPhone, txtFieldEmail;

    private ClientController clientController;
    private OperationHandlerController operationHandlerController;
    private ProjectInformant informant;
    private ActionEvent event;
    private boolean isOperationWindow;
    private Notification notification;
    private ClientService clientService;
    private LoaderFXML loaderFXML;
    private Validator validator;
    private ResourceBundle resources;
    private Client client;
    private Mode mode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(ClientHandlerController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        loaderFXML = LoaderFXML.getInstance();
        validator = Validator.getInstance();
        notification = Notification.getInstance();
        clientService = new ClientService();

        loaderFXML.getMain().getGenders().forEach(g -> comboBoxGender.getItems().add(g.getType()));

        Platform.runLater(this::checkMode);

        Platform.runLater(() -> {
            if (mode != Mode.VIEW) setValidationOnPane();
        });

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }

    public void setController(ClientController controller) {
        this.clientController = controller;
    }

    public void setController(OperationHandlerController controller) {
        this.operationHandlerController = controller;
        isOperationWindow = true;
    }

    @Override
    public void checkMode() {
        switch (((Button) event.getSource()).getId()) {
            case "addClient":
                informant.logInfo("Запущен режим заведения клиента");
                mode = Mode.CREATE;
                labelTitle.setText(resources.getString("title_create_client"));
                btnAction.setText(resources.getString("button_create"));
                labelStatus.setText(resources.getString("creation_status"));
                labelStatus.getStyleClass().add(StateColor.NEW.getState());
                client = new Client();
                break;
            case "editClient":
                informant.logInfo("Запущен режим редактирования клиента");
                mode = Mode.EDIT;
                labelTitle.setText(resources.getString("title_edit_client"));
                btnAction.setText(resources.getString("button_edit"));
                client = clientController.getTableView().getSelectionModel().getSelectedItem();
                showDetails();
                break;
            default:
                informant.logInfo("Запущен режим просмотра клиента");
                mode = Mode.VIEW;
                labelTitle.setText(resources.getString("title_view_client"));
                btnAction.setText(resources.getString("button_view"));
                client = clientController.getTableView().getSelectionModel().getSelectedItem();
                lockFields(true, gridPaneFirst, gridPaneSecond);
                showDetails();
                break;
        }
    }

    @Override
    public void useButtonAction(ActionEvent event) {
        if (mode == Mode.EDIT) update(event);
        else if (mode == Mode.VIEW) report();
        else if (mode == Mode.CREATE) {
            if (isOperationWindow) {
                Client client = create(event);
                operationHandlerController.setClient(client);
                assert client != null;
                operationHandlerController.getTxtAreaClient().setText(
                        resources.getString("serial_number") + " " + client.getPassport().getSerial() + "/" + client.getPassport().getNumber() + "\n" +
                                resources.getString("fio") + " " + client.getSurname() + " " + client.getName() + " " + client.getMiddleName());
            } else create(event);
        }
    }

    @Override
    public Client create(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("is_input_data_correct"))) {
                Client client = contain();
                if (clientService.findBySerialNumber(client.getPassport().getSerial(), client.getPassport().getNumber()) == null) {
                    informant.logInfo("Создание нового клиента");
                    clientService.create(client);
                    loaderFXML.getMain().getClients().add(client);
                    informant.logInfoAndShowNotificationComplete(resources.getString("new_client") + " " + client.getSurname() + " " + client.getName() + " " +
                            resources.getString("successfully_created"));
                    closeWindow(event);
                    return client;
                }
                else informant.logInfoAndShowNotificationWarning(resources.getString("client_with_this_passport_exist"));

            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
        return null;
    }

    @Override
    public void update(ActionEvent event) {
        if (validate(gridPaneFirst, gridPaneSecond)) {
            if (notification.confirmation(resources.getString("do_change_client_data"))) {
                if (clientService.findByIdAndSerialNumber(client.getId(), Long.parseLong(txtFieldSerialPass.getText()), Long.parseLong(txtFieldNumPass.getText())) == null) {
                    contain();
                    informant.logInfo("Редактирование клиента с id " + client.getId());
                    clientService.update(client);
                    clientController.getTableView().refresh();
                    informant.logInfoAndShowNotificationComplete(resources.getString("information_about_client_successfully_updated"));
                    closeWindow(event);
                }
                else informant.logInfoAndShowNotificationWarning(resources.getString("client_with_this_passport_exist"));
            }
        } else notification.warning(resources.getString("fields_have_not_correct_value"));
    }

    @Override
    public void report() {
        Client client = clientController.getTableView().getSelectionModel().getSelectedItem();
        informant.logInfo("Запущена генерация отчета по клиенту с id " + client.getId());
        Platform.runLater(() -> Report.getInstance().clientReport(client));
    }

    @Override
    public Client contain() {
        client.setSurname(txtFieldSurname.getText());
        client.setName(txtFieldName.getText());
        client.setMiddleName(txtFieldMiddleName.getText());
        client.setBirthday(datePickerBirthday.getValue());
        client.setGender(loaderFXML.getMain().getGenders().get(comboBoxGender.getSelectionModel().getSelectedIndex()));

        Contact contact;
        if (mode == Mode.CREATE) contact = new Contact();
        else contact = client.getContact();
        contact.setPhone(txtFieldPhone.getText());
        contact.setEmail(txtFieldEmail.getText());
        client.setContact(contact);

        Passport passport;
        if (mode == Mode.CREATE) passport = new Passport();
        else passport = client.getPassport();
        passport.setSerial(Long.parseLong(txtFieldSerialPass.getText()));
        passport.setNumber(Long.parseLong(txtFieldNumPass.getText()));
        passport.setUnitCode(Integer.parseInt(txtFieldUnitCode.getText()));
        passport.setBirthPlace(txtAreaBirthPlace.getText());
        passport.setReleasedBy(txtAreaReleasedBy.getText());
        passport.setDateReleased(datePickerReleased.getValue());
        passport.setRegistration(txtAreaRegistration.getText());
        client.setPassport(passport);

        Status status;
        if (mode == Mode.CREATE) status = loaderFXML.getMain().getStatuses().get(0);
        else status = client.getStatus();
        client.setStatus(status);

        return client;
    }

    @Override
    public void showDetails() {
        labelStatus.setText(client.getStatus().getName());
        txtFieldSurname.setText(client.getSurname());
        txtFieldName.setText(client.getName());
        txtFieldMiddleName.setText(client.getMiddleName());
        comboBoxGender.getSelectionModel().select(client.getGender().getType());
        datePickerBirthday.setValue(client.getBirthday());
        txtFieldSerialPass.setText(String.valueOf(client.getPassport().getSerial()));
        txtFieldNumPass.setText(String.valueOf(client.getPassport().getNumber()));
        txtFieldUnitCode.setText(String.valueOf(client.getPassport().getUnitCode()));
        txtAreaReleasedBy.setText(client.getPassport().getReleasedBy());
        txtAreaBirthPlace.setText(client.getPassport().getBirthPlace());
        txtAreaRegistration.setText(client.getPassport().getRegistration());
        datePickerReleased.setValue(client.getPassport().getDateReleased());
        txtFieldPhone.setText(client.getContact().getPhone());
        txtFieldEmail.setText(client.getContact().getEmail());

        if (client.getStatus().getName().equals(loaderFXML.getMain().getStatuses().get(0).getName())) {
            labelStatus.getStyleClass().add(StateColor.ACTIVE.getState());
        } else labelStatus.getStyleClass().add(StateColor.ARCHIVE.getState());
    }

    @Override
    public void setValidationOnPane() {
        //TODO переделать валидацию под не резидентов + валидация телефона
        validator.validatePane(btnAction, gridPaneFirst, gridPaneSecond);
        validator.cyrillicAndDash(resources.getString("prompt_surname"), txtFieldSurname);
        validator.onlyCyrillic(resources.getString("prompt_name"), txtFieldName);
        validator.onlyCyrillic(resources.getString("prompt_middle_name"), txtFieldMiddleName);
        validator.comboBoxNotNull(comboBoxGender);
        validator.checkBirthdayAndPassport(resources.getString("prompt_birth_day"), resources.getString("prompt_date_released"), datePickerBirthday, datePickerReleased);
        //validator.checkPhoneNumber(resources.getString("prompt_phone"), txtFieldPhone);
        validator.email(resources.getString("prompt_email"), txtFieldEmail);
        validator.onlyInteger(resources.getString("prompt_serial"), txtFieldSerialPass, 4);
        validator.onlyInteger(resources.getString("prompt_number"), txtFieldNumPass, 6);
        validator.onlyInteger(resources.getString("prompt_unit_code"), txtFieldUnitCode, 6);
        validator.cyrillicAndInteger(resources.getString("prompt_released_by"), txtAreaReleasedBy);
        validator.cyrillicAndInteger(resources.getString("prompt_birth_place"), txtAreaBirthPlace);
        validator.cyrillicAndInteger(resources.getString("prompt_registration"), txtAreaRegistration);
    }
}