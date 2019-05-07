package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import ru.golubyatnikov.money.exchange.Main;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.jar.Manifest;


public class VersionController implements Initializable {

    @FXML
    private Label labelVersion;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        Platform.runLater(this::getVersion);
    }

    private void getVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        try {
            if ((new File("pom.xml")).exists()) {
                model = reader.read(new FileReader("pom.xml"));
                labelVersion.setText(model.getVersion());
            } else {
                model = reader.read(new InputStreamReader(VersionController.class.getResourceAsStream(
                        "/META-INF/maven/ru.golubyatnikov.money.exchange/Exchanger/pom.xml")));
                labelVersion.setText(model.getVersion());
            }
        } catch (IOException | XmlPullParserException e) {
            labelVersion.setText(resources.getString("unable_to_load_current_version_of_program"));
            e.printStackTrace();
        }
    }
}