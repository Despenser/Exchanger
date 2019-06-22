package ru.golubyatnikov.money.exchange.model.util;


import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.golubyatnikov.money.exchange.model.enumirate.IsoCode;
import java.util.concurrent.atomic.AtomicInteger;


public class ConfigComboBox extends ListCell<IsoCode> {

    private static final String PATH_IMG = "view/style/img/currency/";
    private static final String EXTENSION = ".png";

    @SafeVarargs
    public static void configComboBox(ComboBox<IsoCode>... comboBox) {
        AtomicInteger i = new AtomicInteger(0);
        for (ComboBox<IsoCode> box : comboBox) {
            box.getItems().addAll(IsoCode.values());
            box.setCellFactory(listView -> new ConfigComboBox());
            box.setButtonCell(new ConfigComboBox());
            box.getSelectionModel().select(i.get());
            i.getAndIncrement();
        }
    }

    @Override
    protected void updateItem(IsoCode item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setItem(null);
            setGraphic(null);
        } else {
            setText(item.name());
            Label label = new Label(null, new ImageView(new Image(PATH_IMG + item.name() + EXTENSION)));
            setGraphic(label);
        }
    }

    public static void ifSameCurrency(ComboBox<IsoCode> first, ComboBox<IsoCode> second, IsoCode oldValue, IsoCode newValue){
        if (!second.getSelectionModel().isEmpty() && second.getSelectionModel().getSelectedItem().equals(newValue)){

            if (oldValue != null) second.getSelectionModel().select(oldValue);
            else if (second.getSelectionModel().isSelected(second.getItems().size() - 1) && !first.getSelectionModel().isEmpty())
                second.getSelectionModel().selectPrevious();
            else second.getSelectionModel().selectNext();

            first.getSelectionModel().select(newValue);
        }
    }
}