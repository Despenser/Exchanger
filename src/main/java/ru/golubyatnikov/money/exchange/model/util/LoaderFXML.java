package ru.golubyatnikov.money.exchange.model.util;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.golubyatnikov.money.exchange.Main;
import java.io.IOException;
import java.util.*;


public class LoaderFXML {

    private static final String FXML_DIR = "/view/fxml/";
    private static final String BUNDLE = "bundle/localization";
    private static final String FXML_EXTENSION = ".fxml";
    private static final String LOGO = "/view/style/img/common/other/logo.png";
    private static LoaderFXML instance;

    private Main main;

    private LoaderFXML() {
    }

    public static LoaderFXML getInstance() {
        if (instance == null) {
            synchronized (LoaderFXML.class) {
                if (instance == null) instance = new LoaderFXML();
            }
        }
        return instance;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    private FXMLLoader loader(String nameFXML) {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle(BUNDLE, Locale.getDefault()));
        loader.setLocation(LoaderFXML.class.getResource(FXML_DIR + nameFXML + FXML_EXTENSION));
        return loader;
    }

    private Parent loadParent(String nameFXML) {
        try {
            return loader(nameFXML).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadRoot(Stage stage, String nameFXML, String title, double minWidth, double minHeight, boolean resizable) {
        Parent parent = Objects.requireNonNull(loadParent(nameFXML));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setResizable(resizable);
        stage.getIcons().add(new Image(LOGO));
        stage.show();
    }

    public <T> List<Object> loadToolbar(String nameFXML) {
        try {
            FXMLLoader loader = loader(nameFXML);
            AnchorPane pane = loader.load();
            T controller = loader.getController();
            return new ArrayList<>(Arrays.asList(pane, controller));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadScene(String nameFXML, Pane pane) {
        try {
            FXMLLoader loader = loader(nameFXML);
            pane.getChildren().setAll((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T loadModalWindow(String nameFXML, String title, boolean resizable) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = loader(nameFXML);
            Parent parent = loader.load();
            T controller = loader.getController();
            stage.getIcons().add(new Image(LOGO));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.setResizable(resizable);
            Platform.runLater(stage::showAndWait);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<Object> loadSettingTitlePane(String nameFXML) {
        try {
            FXMLLoader loader = loader(nameFXML);
            TitledPane tab = loader.load();
            T controller = loader.getController();
            return new ArrayList<>(Arrays.asList(tab, controller));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}