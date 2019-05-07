package ru.golubyatnikov.money.exchange.model.util;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.lang.reflect.Field;
import javafx.util.Duration;
import java.util.Optional;


public class Notification {

    private static final String PATH_IMG = "view/style/img/notification/";
    private static final String PATH_STYLE = "view/style/css/style.css";

    private static Notification instance;

    private Notification() {
    }

    public static Notification getInstance() {
        if (instance == null) {
            synchronized (Notification.class) {
                if (instance == null) instance = new Notification();
            }
        }
        return instance;
    }

    private Alert getAlert(Alert.AlertType type, String title,  String content, String pathTitleImage, String pathImage, ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().getStylesheets().add(PATH_STYLE);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText("");
        alert.getButtonTypes().setAll(buttonTypes);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(pathTitleImage));
        alert.setGraphic(new ImageView(pathImage));
        return alert;
    }

    public void complete(String msg) {
        Platform.runLater(getAlert(Alert.AlertType.INFORMATION,"Положительный результат", msg,
                PATH_IMG + "complete.png", PATH_IMG + "completeBig.png",
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE))::showAndWait
        );
    }

    public void warning(String msg) {
        Platform.runLater(getAlert(Alert.AlertType.WARNING,"Предупреждение", msg,
                PATH_IMG + "warning.png", PATH_IMG + "warningBig.png",
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE))::showAndWait
        );
    }

    public void info(String msg) {
        Platform.runLater(getAlert(Alert.AlertType.INFORMATION,"Информация", msg,
                PATH_IMG + "info.png", PATH_IMG + "infoBig.png",
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE))::showAndWait
        );
    }

    public boolean confirmation(String msg){
        Alert alert = getAlert(Alert.AlertType.CONFIRMATION, "Подтверждение действия", msg,
                PATH_IMG + "question.png", PATH_IMG + "questionBig.png",
                new ButtonType("Да", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE));
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    public Tooltip tooltip(String msg) {
        Tooltip tooltip = new Tooltip();
        tooltip.setId("tooltip");
        tooltip.setText(msg);
        tooltip.setFont(Font.font("", 12));
        hackReactionTimer(tooltip);
        return tooltip;
    }

    public Tooltip tooltip(String msg, String image) {
        Tooltip tooltip = new Tooltip();
        tooltip.setId("tooltip");
        tooltip.setText(msg);
        tooltip.setGraphic(new ImageView(new Image(PATH_IMG + image)));
        tooltip.setFont(Font.font("", 12));
        hackReactionTimer(tooltip);
        return tooltip;
    }

    private void hackReactionTimer(Tooltip tooltip){
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}