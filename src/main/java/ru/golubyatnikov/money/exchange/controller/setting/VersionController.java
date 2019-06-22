package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class VersionController implements Initializable {

    @FXML private Label labelVersion;

    private ResourceBundle resources;
    private ProjectInformant informant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(VersionController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;
        Platform.runLater(this::getVersion);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    private void getVersion() {
        informant.logInfo("Процесс получения текущей версии программы");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        try {
            if ((new File("pom.xml")).exists()) {
                model = reader.read(new FileReader("pom.xml"));
                getAndShowCurrentVersion(model);
            } else {
                model = reader.read(new InputStreamReader(
                        VersionController.class.getResourceAsStream("/META-INF/maven/ru.golubyatnikov.money.exchange/Exchanger/pom.xml")));
                getAndShowCurrentVersion(model);
            }
        } catch (Exception e) {
            String message = resources.getString("unable_to_load_current_version_of_program");
            informant.logErrorAndShowNotificationWarning(message, e);
            labelVersion.setText(message);
        }
    }

    private void getAndShowCurrentVersion(Model model){
        String version = model.getVersion();
        informant.logInfo("Текущая версия программы: " + version);
        labelVersion.setText(version);
    }
}