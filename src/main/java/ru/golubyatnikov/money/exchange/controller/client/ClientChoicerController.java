package ru.golubyatnikov.money.exchange.controller.client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.golubyatnikov.money.exchange.controller.operation.OperationHandlerController;
import ru.golubyatnikov.money.exchange.model.entity.Client;
import ru.golubyatnikov.money.exchange.model.service.ClientService;
import ru.golubyatnikov.money.exchange.model.util.LoaderFXML;
import java.net.URL;
import java.util.ResourceBundle;


public class ClientChoicerController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ClientChoicerController.class);

    @FXML private TableView<Client> tableView;
    @FXML private TableColumn<Client, String> colSurname, colName, colMiddleName;
    @FXML private TableColumn<Client, Long> colSerial, colNum;
    @FXML private TextField txtFieldSearch;

    private OperationHandlerController operationHandlerController;
    private ObservableList<Client> noArchiveClients;
    private ClientService clientService;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        clientService = new ClientService();

        tableView.setPlaceholder(new Label(resources.getString("table_view_placeholder_choice_client")));

        colSerial.setCellValueFactory(cellData -> cellData.getValue().getPassport().serialProperty().asObject());
        colNum.setCellValueFactory(cellData -> cellData.getValue().getPassport().numberProperty().asObject());
        colSurname.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colMiddleName.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> elasticSearch());

        Platform.runLater(this::selectClient);
        Platform.runLater(this::getAll);

        LOG.info("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    private void selectClient(){
        tableView.setRowFactory( tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    LOG.info("Выбран клиент из списка с id " + row.getItem().getId());
                    addClient(event, row.getItem());
                }
            });
            return row;
        });
    }

    public void setOperationHandlerController(OperationHandlerController controller) {
        this.operationHandlerController = controller;
    }

    private void setNoArchiveClients(ObservableList<Client> noArchiveClients) {
        this.noArchiveClients = noArchiveClients;
    }

    private void addClient(MouseEvent event, Client client) {
        operationHandlerController.setClient(client);
        operationHandlerController.getTxtAreaClient().setText(
                resources.getString("serial_number") + " " + client.getPassport().getSerial() + "/" + client.getPassport().getNumber() + "\n" +
                resources.getString("fio") + " " + client.getSurname() + " " + client.getName() + " " + client.getMiddleName());
        ((Stage)((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void populateTableView(ObservableList<Client> clientList) {
        noArchiveClients = FXCollections.observableArrayList();
        for (Client client: clientList) {
            if (client.getStatus().getName().equals(LoaderFXML.getInstance().getMain().getStatuses().get(0).getName()))
                noArchiveClients.add(client);
        }
        tableView.setItems(noArchiveClients);
    }

    @FXML
    private void clearSearch() {
        txtFieldSearch.clear();
    }

    private void getAll() {
        populateTableView(FXCollections.observableArrayList(clientService.findAll()));
    }

    private void elasticSearch() {
        if (txtFieldSearch.getText().isEmpty()) getAll();
        ObservableList<Client> clients = FXCollections.observableArrayList(clientService.findChoicerPane(txtFieldSearch.getText()));
        if (clients.isEmpty()) tableView.setPlaceholder(new Label(resources.getString("new_table_view_placeholder_client")));
        setNoArchiveClients(clients);
        populateTableView(noArchiveClients);
    }
}