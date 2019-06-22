package ru.golubyatnikov.money.exchange.controller.report;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import ru.golubyatnikov.money.exchange.model.util.ProjectInformant;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class PreviewController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private StackPane stackPane;
    @FXML private ImageView reportView;
    @FXML private ComboBox<Integer> pageList;
    @FXML private Slider zoomLevel;

    private double zoomFactor, imageHeight, imageWidth;
    private ProjectInformant informant;
    private JasperPrint jasperPrint;
    private ResourceBundle resources;
    private List<Integer> pages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informant = new ProjectInformant(PreviewController.class);
        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName());

        this.resources = resources;

        Platform.runLater(this::configureComponents);
        Platform.runLater(this::changeScalePage);
        Platform.runLater(this::pageTurnScroll);

        informant.logInfo("Инициализация класса " + this.getClass().getSimpleName() + " завершена");
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    private void configureComponents(){
        zoomFactor = 1d;
        zoomLevel.setValue(100d);
        imageHeight = jasperPrint.getPageHeight();
        imageWidth = jasperPrint.getPageWidth();

        zoomLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            reportView.setFitHeight(imageHeight * zoomFactor);
            reportView.setFitWidth(imageWidth * zoomFactor);
        });

        stackPane.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                scrollPane.getViewportBounds().getWidth(), scrollPane.viewportBoundsProperty()));

        if (jasperPrint.getPages().size() > 0) {
            viewPage(0);
            pages = new ArrayList<>();
            for (int i = 0; i < jasperPrint.getPages().size(); i++) pages.add(i + 1);
        }
        pageList.setItems(FXCollections.observableArrayList(pages));
        pageList.getSelectionModel().select(0);
    }

    private void changeScalePage(){
        reportView.setOnScroll(event -> {
            if (event.getDeltaY() > 0 && event.isControlDown()){
                zoomLevel.setValue(zoomLevel.getValue() + 15);
                zoomFactor = zoomLevel.getValue() / 100;
                reportView.setFitHeight(imageHeight * zoomFactor);
                reportView.setFitWidth(imageWidth * zoomFactor);
            }
            else if (event.getDeltaY() < 0 && event.isControlDown()){
                zoomLevel.setValue(zoomLevel.getValue() - 15);
                zoomFactor = zoomLevel.getValue() / 100;
                reportView.setFitHeight(imageHeight * zoomFactor);
                reportView.setFitWidth(imageWidth * zoomFactor);
            }
        });
    }

    private void pageTurnScroll(){
        stackPane.setOnScroll(event -> {
            if (event.getDeltaY() > 0 && !event.isControlDown()) {
                pageList.getSelectionModel().selectPrevious();
                viewPage(pageList.getValue() - 1);
            }
            else if (event.getDeltaY() < 0 && !event.isControlDown()){
                pageList.getSelectionModel().selectNext();
                viewPage(pageList.getValue() - 1);
            }
        });
    }

    @FXML
    private void save() {
        informant.logInfo("Запущена процедура сохранения отчета");
        FileChooser fileChooser = getFileChooserWithExtensions();
        File file = fileChooser.showSaveDialog(new Stage());
        if (fileChooser.getSelectedExtensionFilter() != null && fileChooser.getSelectedExtensionFilter().getExtensions() != null) {
            List<String> selectedExt = fileChooser.getSelectedExtensionFilter().getExtensions();
            if (selectedExt.contains("*.pdf")) exportToFile(new JRPdfExporter(), file);
            else if (selectedExt.contains("*.docx")) exportToFile(new JRDocxExporter(), file);
            else if (selectedExt.contains("*.xlsx")) exportToFile(new JRXlsxExporter(), file);
        }
    }

    private FileChooser getFileChooserWithExtensions(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("save_file"));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PDF Document", Arrays.asList("*.pdf", "*.PDF")),
                new ExtensionFilter("DOCX Document", Arrays.asList("*.docx", "*.DOCX")),
                new ExtensionFilter("XLSX Document", Arrays.asList("*.xlsx", "*.XLSX")));
        return fileChooser;
    }

    private <T extends JRAbstractExporter> void exportToFile(T exporter, File file) {
        try {
            informant.logInfo("Процесс экспортирования отчета в файл");
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getAbsolutePath()));
            exporter.exportReport();
            informant.logInfo("Файл создан");
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void print() {
        informant.logInfo("Запущена процедура печати отчета");
        Platform.runLater(()-> {
            try {
                JasperPrintManager.printReport(jasperPrint, true);
            } catch (JRException e) {
                informant.logErrorAndShowNotificationWarning("При печати отчета произошел сбой", e);
            }
        });
    }

    @FXML
    private void pageListSelected() {
        viewPage(pageList.getSelectionModel().getSelectedItem() - 1);
    }

    private WritableImage getImage(int pageNumber) throws JRException {
        BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber, 2);
        WritableImage fxImage = new WritableImage(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
        return SwingFXUtils.toFXImage(image, fxImage);
    }

    private void viewPage(int pageNumber) {
        try {
            reportView.setFitHeight(imageHeight * zoomFactor);
            reportView.setFitWidth(imageWidth * zoomFactor);
            reportView.setImage(getImage(pageNumber));
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}