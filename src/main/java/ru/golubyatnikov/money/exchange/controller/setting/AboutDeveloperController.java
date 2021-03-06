package ru.golubyatnikov.money.exchange.controller.setting;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


public class AboutDeveloperController implements Initializable {

    private static final String VK = "https://vk.com/alex.euphoria";
    private static final String GIT_HUB = "https://github.com/Despenser";
    private static final String MAIL = "mailto:agolubyatnikov94@gmail.com";

    @FXML private Label labelSurname, labelName, labelMiddleName, labelCopyrating, labelAbout;
    @FXML private Hyperlink linkVK, linkGitHub, linkEmail;
    @FXML private ImageView imgDeveloper;

    private ProjectInformant informant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(AboutDeveloperController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        imgDeveloper.setImage(new Image("view/style/img/common/other/developer.jpg"));
        labelSurname.setText(resources.getString("developer_surname"));
        labelName.setText(resources.getString("developer_name"));
        labelMiddleName.setText(resources.getString("developer_middle_name"));
        labelAbout.setText("\t" + resources.getString("about_developer"));
        labelCopyrating.setText(resources.getString("copyrating"));

        linkVK.setText("https://vk.com/alex.euphoria");
        linkGitHub.setText("https://github.com/Despenser");
        linkEmail.setText("agolubyatnikov94@gmail.com");

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    @FXML
    private void linkToGithub() {
        openLinkInBrowser(GIT_HUB);
    }

    @FXML
    private void linkToVK() {
        openLinkInBrowser(VK);
    }

    @FXML
    private void linkToEmail() {
        openLinkInBrowser(MAIL);
    }

    private void openLinkInBrowser(String url){
        informant.logInfo("Процесс открытия ссылки " + url);
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                informant.logInfo("Ссылка " + url + " открыта");
            } catch (IOException | URISyntaxException e) {
                informant.logErrorAndShowNotificationWarning("Во время открытия ссылки произошел сбой", e);
            }
        }
    }
}