package ru.golubyatnikov.money.exchange.controller.client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.golubyatnikov.money.exchange.controller.AbstractController;
import ru.golubyatnikov.money.exchange.model.entity.Client;
import ru.golubyatnikov.money.exchange.model.entity.Operation;
import ru.golubyatnikov.money.exchange.model.entity.Status;
import ru.golubyatnikov.money.exchange.model.service.ClientService;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import ru.golubyatnikov.money.exchange.model.util.Notification;
import ru.golubyatnikov.money.exchange.model.util.Report;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


public class ClientController extends AbstractController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ClientController.class);

    @FXML private ToolBar toolbar;
    @FXML private TableView<Client> tableView;
    @FXML private DatePicker datePickerBirthday, datePickerReleased;
    @FXML private Button addClient, viewClient, editClient, deleteClient, archiveClient, reportClient, clearSearch;
    @FXML private TableColumn<Client, Long> colSerial, colNumber;
    @FXML private TableColumn<Client, String> colSurname, colName, colMiddleName, colGender, colBirthPlace, colPhone, colEmail, colStatus;
    @FXML private TextField txtFieldSearch, txtFieldSurname, txtFieldName, txtFieldMiddleName, txtFieldGender, txtFieldSerialPass,
            txtFieldNumPass, txtFieldReleasedBy, txtFieldUnitCode, txtFieldBirthPlace, txtFieldRegistration, txtFieldPhone, txtFieldEmail;

    private ClientService clientService;
    private Notification notification;
    private LoaderFXML loaderFXML;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        clientService = new ClientService();
        notification = Notification.getInstance();
        loaderFXML = LoaderFXML.getInstance();

        if (loaderFXML.getMain().isAdmin()) toolbar.getItems().remove(deleteClient);
        if (loaderFXML.getMain().getCurrentEmployee().getRole().getType().equals(loaderFXML.getMain().getRoles().get(2).getType())){
            toolbar.getItems().removeAll(addClient, editClient, deleteClient, archiveClient);
        }

        datePickerBirthday.setOnMouseClicked(e -> { if (!datePickerBirthday.isEditable()) datePickerBirthday.hide(); });
        datePickerReleased.setOnMouseClicked(e -> { if (!datePickerReleased.isEditable()) datePickerReleased.hide(); });

        tableView.setPlaceholder(new Label(resources.getString("table_view_placeholder_client")));

        addClient.setTooltip(notification.tooltip(resources.getString("add_client")));
        viewClient.setTooltip(notification.tooltip(resources.getString("view_client")));
        editClient.setTooltip(notification.tooltip(resources.getString("edit_client")));
        deleteClient.setTooltip(notification.tooltip(resources.getString("delete_client")));
        archiveClient.setTooltip(notification.tooltip(resources.getString("change_client_status")));
        reportClient.setTooltip(notification.tooltip(resources.getString("report_client")));
        clearSearch.setTooltip(notification.tooltip(resources.getString("clear_search")));

        colSurname.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colMiddleName.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().getGender().typeProperty());
        colSerial.setCellValueFactory(cellData -> cellData.getValue().getPassport().serialProperty().asObject());
        colNumber.setCellValueFactory(cellData -> cellData.getValue().getPassport().numberProperty().asObject());
        colBirthPlace.setCellValueFactory(cellData -> cellData.getValue().getPassport().birthPlaceProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().getContact().phoneProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().getContact().emailProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().getStatus().nameProperty());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> show(newValue));

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> elasticSearch());

        Platform.runLater(this::getAll);

        LOG.info("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    TableView<Client> getTableView() {
        return tableView;
    }

    @Override
    public void add(ActionEvent event) {
        LOG.info("Открытие формы заведения клиента");
        openHandler(resources.getString("form_create_client"), event);
    }

    @Override
    public void view(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_view_client"))) {
            LOG.info("Открытие формы просмотра клиента");
            openHandler(resources.getString("form_view_client"), event);
        }
    }

    @Override
    public void edit(ActionEvent event) {
        if (checkSelected(tableView, resources.getString("select_for_edit_client"))) {
            LOG.info("Открытие формы редактирования клиента");
            openHandler(resources.getString("form_edit_client"), event);
        }
    }

    @Override
    public void delete() {
        if (checkSelected(tableView, resources.getString("select_for_delete_client"))) {
            Client client = tableView.getSelectionModel().getSelectedItem();
            if (client.getOperations().isEmpty()) {
                boolean result = notification.confirmation(resources.getString("do_delete_client"));
                if (result) {
                    LOG.info("Запущена процедура удаления клиента с id " + client.getId() + ", без операций");
                    delete(client, clientService, loaderFXML.getMain().getClients(), tableView);
                }
            } else {
                boolean result = notification.confirmation(resources.getString("client_has_operations"));
                if (result) {
                    Set<Operation> operations = client.getOperations();
                    LOG.info("Запущена процедура удаления клиента с id " + client.getId() + ", количество операций у клиента " + operations.size());
                    operations.forEach(operation -> operation.getCurrencies().clear());
                    delete(client, clientService, loaderFXML.getMain().getClients(), tableView);
                }
            }
        }
    }

    @Override
    public void openHandler(String title, ActionEvent event) {
        ClientHandlerController controller = loaderFXML.loadModalWindow("client/editorClient", title, false);
        controller.setController(this);
        controller.setEvent(event);
    }

    @FXML
    private void archive() {
        if (checkSelected(tableView, resources.getString("select_for_archive_client"))) {
            Client client = tableView.getSelectionModel().getSelectedItem();
            Status active = loaderFXML.getMain().getStatuses().get(0);
            Status archive = loaderFXML.getMain().getStatuses().get(1);

            if (client.getStatus().getName().equals(active.getName())) {
                LOG.info("Смена статуса у клиента с id " + client.getId() + " на \"В архиве\"");
                updateStatus(client, clientService, tableView, archive, resources.getString("change_client_status_on_archive"));
            }
            else {
                LOG.info("Смена статуса у клиента с id " + client.getId() + " на \"Активный\"");
                updateStatus(client, clientService, tableView, active, resources.getString("change_client_status_on_active"));
            }
        }
    }

    @Override
    public void report() {
        if (checkSelected(tableView, resources.getString("select_for_report_client"))) {
            Client client = tableView.getSelectionModel().getSelectedItem();
            LOG.info("Запущена генерация отчета по клиенту с id " + client.getId());
            Platform.runLater(() -> Report.getInstance().clientReport(client));
        }
    }

    @Override
    public void clearSearch() {
        txtFieldSearch.clear();
    }

    @Override
    public void getAll() {
        populateTable(tableView, loaderFXML.getMain().getClients());
    }

    @Override
    public void elasticSearch() {
        if (txtFieldSearch.getText().isEmpty()) getAll();
        ObservableList<Client> clients = FXCollections.observableArrayList(clientService.findClientPane(txtFieldSearch.getText()));
        if (clients.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("new_table_view_placeholder_client")));
        loaderFXML.getMain().setClients(clients);
        populateTable(tableView, loaderFXML.getMain().getClients());
    }

    private void show(Client client) {
        if (client != null) {
            LOG.info("Фокус установлен на клиенте с id " + client.getId());
            txtFieldSurname.setText(client.getSurname());
            txtFieldName.setText(client.getName());
            txtFieldMiddleName.setText(client.getMiddleName());
            txtFieldGender.setText(client.getGender().getType());
            datePickerBirthday.setValue(client.getBirthday());
            txtFieldSerialPass.setText(String.valueOf(client.getPassport().getSerial()));
            txtFieldNumPass.setText(String.valueOf(client.getPassport().getNumber()));
            txtFieldUnitCode.setText(String.valueOf(client.getPassport().getUnitCode()));
            txtFieldReleasedBy.setText(client.getPassport().getReleasedBy());
            txtFieldBirthPlace.setText(client.getPassport().getBirthPlace());
            txtFieldRegistration.setText(client.getPassport().getRegistration());
            datePickerReleased.setValue(client.getPassport().getDateReleased());
            txtFieldPhone.setText(client.getContact().getPhone());
            txtFieldEmail.setText(client.getContact().getEmail());
        }
    }
}