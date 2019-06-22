package ru.golubyatnikov.money.exchange.model.util;


import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.regex.Pattern;


public class Validator {

    private static final String WRONG_FIELD = "wrong-fields";
    private static final String GOOD_FIELD = "good-fields";
    private static final String IMAGE_FOR_HELP = "help.png";
    private static volatile Validator instance;
    private Notification notification;

    private Validator() {
        notification = Notification.getInstance();
    }

    public static Validator getInstance() {
        if (instance == null) {
            synchronized (Validator.class) {
                if (instance == null) instance = new Validator();
            }
        }
        return instance;
    }

    public void validatePane(Button button, GridPane... gridPanes) {
        button.setDisable(true);
        for (GridPane pane : gridPanes) {
            for (Node child : pane.getChildren()) {
                if (child instanceof TextInputControl) {
                    ((TextInputControl) child).textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.trim().isEmpty() && isAllFilled(gridPanes)) button.setDisable(false);
                        else button.setDisable(true);
                    });
                }
                if (child instanceof ComboBoxBase) {
                    ((ComboBoxBase<?>) child).valueProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null && isAllFilled(gridPanes)) button.setDisable(false);
                        else button.setDisable(true);
                    });
                }
            }
        }
    }

    private boolean isAllFilled(GridPane... tables) {
        for (GridPane table : tables) {
            for (Node node : table.getChildren()) {
                if (node instanceof TextInputControl)
                    if (((TextInputControl) node).getText().trim().isEmpty() && !node.getId().equals("txtFieldMiddleName")) return false;
                if (node instanceof ComboBoxBase) if (((ComboBoxBase) node).getValue() == null) return false;
            }
        }
        return true;
    }

    @SafeVarargs
    private final <T extends Node> void setWrongStyle(T... nodes) {
        for (T node : nodes) {
            node.getStyleClass().removeAll(GOOD_FIELD);
            node.getStyleClass().add(WRONG_FIELD);
        }
    }

    @SafeVarargs
    private final <T extends Node> void setGoodStyle(T... nodes) {
        for (T node : nodes) {
            node.getStyleClass().removeAll(WRONG_FIELD);
            node.getStyleClass().add(GOOD_FIELD);
        }
    }

    private <T extends TextInputControl> void setFieldColor(T field, String pattern, String value) {
        if (!field.getText().isEmpty() && value.matches(pattern)) setGoodStyle(field);
        else setWrongStyle(field);
    }

    private void commonValidator(String promptText, TextInputControl field, String regex, String ifNotMatchesRegex) {
        field.setPromptText(promptText);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.textProperty().addListener((obs, oldV, newV) -> {
                    if (!newV.matches(regex)) {
                        field.setText(newV.replaceAll(ifNotMatchesRegex, ""));
                        addNotification(field,"Пример заполнения: " + promptText);
                    }
                    else removeNotification(field);
                    setFieldColor(field, regex, newV);
                });
            }
        });
    }

    private void commonValidator(String promptText, TextInputControl field, int length) {
        field.setPromptText(promptText);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.textProperty().addListener((obs, oldV, newV) -> {
                    if (!newV.matches("\\d{" + length + "}")) {
                        field.setText(newV.replaceAll("[^\\d]", ""));
                        addNotification(field,"Пример заполнения: " + promptText);
                    }
                    setFieldColor(field, "\\d{" + length + "}", newV);
                    removeNotification(field);
                });
            }
        });
    }

    public boolean checkWrongFields(GridPane... gridPanes) {
        for (GridPane pane : gridPanes) {
            for (Node child : pane.getChildren())
                if (child.getStyleClass().contains(WRONG_FIELD)) return false;
        }
        return true;
    }

    public <T extends TextInputControl> void onlyCyrillic(String promptText, T field) {
        commonValidator(promptText, field, "^\\p{IsCyrillic}+$", "[^\\p{IsCyrillic}+$]");
    }

    public <T extends TextInputControl> void cyrillicOrNothing(String promptText, T field) {
        // TODO либо кирилица либо ничего
        // commonValidator(promptText, field, "^\\p{IsCyrillic}+$", "[^\\p{IsCyrillic}+$]");
    }

    public <T extends TextInputControl> void cyrillicAndDash(String promptText, T field) {
        commonValidator(promptText, field, "^[а-яёА-ЯЁ]+[-?]?[а-яёА-ЯЁ]+$",
                "[^[a-zA-Z0-9~;:`!@#№\\$\\%\\^&\\*\\(\\)_+=\\{\\}\\[\\]><\\?/|\\.,'\"]*$]");
    }

    public <T extends TextInputControl> void cyrillicAndInteger(String promptText, T field) {
        commonValidator(promptText, field, "[0-9а-яёА-ЯЁ\\.\\/\\s]*", "^[0-9а-яёА-ЯЁ\\.\\/\\s]$");
    }

    public <T extends TextInputControl> void latinAndInteger(String promptText, T field) {
        commonValidator(promptText, field, "^[a-zA-Z0-9]+$",
                "[^[а-яёА-ЯЁ~;:`!@#№\\$*\\%\\^&\\*\\(\\)_+=\\{\\}\\[\\]><\\?/|\\.,'\"\\s*]+$]");
    }

    public <T extends TextInputControl> void onlyInteger(String promptText, T field, int length) {
        commonValidator(promptText, field, length);
    }

    public void onlyFloat(int numerator, int denominator, TextField... fields) {
        Pattern pattern = Pattern.compile("\\d{0," + numerator + "}([.]\\d{0," + denominator + "})?");
        for (TextField field : fields) {
            field.setTextFormatter(new TextFormatter<>(change -> pattern.matcher(change.getControlNewText()).matches() ? change : null));
        }
    }

    public <T extends TextInputControl> void licenceBank(String promptText, T field) {
        commonValidator(promptText, field, "^[0-9]{1,4}+[-?]{0,1}?[А-Я]{0,1}+$", "[^[a-zA-Z~;:`!@#№\\$\\%\\^&\\*\\(\\)_+=\\{\\}\\[\\]><\\?/|\\.,'\"]*$]");
    }

    public <T extends TextInputControl> void okatoBank(String promptText, T field) {
        commonValidator(promptText, field, "^[0-9]{8,11}+$", "[^\\d]");
    }

    public <T extends TextInputControl> void email(String promptText, T field) {
        commonValidator(promptText, field, "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$",
                "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");
    }

    public void checkEqualsPasswords(String promptText, int length, PasswordField first, PasswordField second) {
        first.setPromptText(promptText);
        second.setPromptText(promptText);
        first.textProperty().addListener(((observable, oldValue, newValue) -> {
            isSamePassword(length, first, second);
            second.textProperty().addListener(((obs, oldV, newV) -> isSamePassword(length, first, second)));
        }));
    }

    private <T extends TextInputControl> void isSamePassword(int length, T first, T second) {
        if (first.getText().length() >= length && second.getText().length() >= length && first.getText().equals(second.getText())) {
            setGoodStyle(first, second);
            removeNotification(first);
            removeNotification(second);
        }
        else {
            setWrongStyle(first, second);
            addNotification(first,"Пароли не совпадают или разной длины");
            addNotification(second,"Пароли не совпадают или разной длины");
        }
    }

    public void checkBirthdayAndPassport(String promptTextBirthday, String promptTextPassport, DatePicker birthday, DatePicker passDate) {
        birthday.setPromptText(promptTextBirthday);
        passDate.setPromptText(promptTextPassport);
        birthday.valueProperty().addListener((observable, oldValue, newValue) -> {
            isCorrectPassport(birthday, passDate);
            passDate.valueProperty().addListener((obs, oldV, newV) -> isCorrectPassport(birthday, passDate));
        });
    }

    private void isCorrectPassport(DatePicker birthday, DatePicker passDate) {
        if (checkPassport(birthday.getValue(), passDate.getValue())) {
            setGoodStyle(birthday, passDate);
            removeNotification(birthday);
            removeNotification(passDate);
        }
        else {
            setWrongStyle(birthday, passDate);
            addNotification(birthday,"Дата рождения связана с датой дата выдачи паспорта и автоматически проверяет просрочен ли паспорт");
            addNotification(passDate, "Дата выдачи паспорта связана с датой дата рождения и автоматически проверяет просрочен ли паспорт");
        }
    }

    private boolean checkPassport(LocalDate birthDay, LocalDate passDate) {
        if (birthDay == null || passDate == null) return false;
        LocalDate dateNow = LocalDate.now();

        LocalDate birthday14 = birthDay.plusYears(14);
        LocalDate birthday20 = birthDay.plusYears(20);
        LocalDate birthday45 = birthDay.plusYears(45);

        if (birthDay.getYear() == passDate.getYear()) return false;
        if ((birthday14.compareTo(dateNow) <= 0) && (birthday14.compareTo(passDate) > 0)) return false;
        if ((birthday20.compareTo(dateNow) <= 0) && (birthday20.compareTo(passDate) > 0)) return false;
        return (birthday45.compareTo(dateNow) > 0) || (birthday45.compareTo(passDate) <= 0);
    }

    public void comboBoxNotNull(ComboBox<?> box) {
        box.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!box.getSelectionModel().isEmpty()) setGoodStyle(box);
            else {
                setWrongStyle(box);
                box.setTooltip(notification.tooltip("Выпадающий список обязателен к заполнению", IMAGE_FOR_HELP));
            }
        });
    }

    public void fieldsNotNull(String promptText, int length, TextField... fields) {
        for (TextField field : fields) {
            field.setPromptText(promptText);
            field.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    field.textProperty().addListener((obs, oldV, newV) -> {
                        if (field.getText().length() < length) {
                            setWrongStyle(field);
                            addNotification(field, "Поле обязательно к заполнению");
                        } else {
                            setGoodStyle(field);
                            removeNotification(field);
                        }
                    });
                }
            });
        }
    }

    public void datePickerNotNull(String promptText, DatePicker picker) {
        picker.setPromptText(promptText);
        picker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (picker.getEditor().getText().matches("(0?[1-9]|[12][0-9]|3[01])\\.(0?[1-9]|1[012])\\.((19|20)\\d\\d)")) {
                setGoodStyle(picker);
                removeNotification(picker);
            }
            else {
                setWrongStyle(picker);
                addNotification(picker, "Поле обязательно к заполнению");
            }
        });
    }

    private void addNotification(Control control, String message){
        Platform.runLater(() -> control.setTooltip(notification.tooltip(message, IMAGE_FOR_HELP)));
    }

    private void removeNotification(Control control){
        try {
            Platform.runLater(() -> control.setTooltip(null));
        } catch (NullPointerException ignored){}
    }

    public void checkPhoneNumber(String promptText, TextField field) {
        field.setPromptText(promptText);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.setPrefColumnCount(13);
                field.setTextFormatter(new TextFormatter<>(this::addPhoneNumberMask));
            }
            if (!newValue) {
                if (!field.getText().matches(".*\\d+.*")) {
                    field.setTextFormatter(null);
                    field.clear();
                    setWrongStyle(field);
                    addNotification(field,"Пример заполнения: " + promptText);
                }
                else if(field.getText().matches("^(\\+7|8)\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$")) {
                    setGoodStyle(field);
                    removeNotification(field);
                }
            }
        });
    }

    //___-___-__-__
    private TextFormatter.Change addPhoneNumberMask(TextFormatter.Change change) {

        if (!change.isContentChange() && !change.getControlNewText().isEmpty()) return change;

        String text = change.getControlNewText();
        int start = change.getRangeStart();
        int end = change.getRangeEnd();

        int anchor = change.getAnchor();
        int caret = change.getCaretPosition();

        StringBuilder phone = new StringBuilder(text);

        int dash;
        while ((dash = phone.lastIndexOf("-")) >= start) {
            phone.deleteCharAt(dash);
            if (caret > dash) caret--;
            if (anchor > dash) anchor--;
        }

        while (phone.length() < 3) phone.append('_');

        if (phone.length() == 3 || phone.charAt(3) != '-') {
            phone.insert(3, '-');
            if (caret > 3 || (caret == 3 && end <= 3 && change.isDeleted())) caret++;
            if (anchor > 3 || (anchor == 3 && end <= 3 && change.isDeleted())) anchor++;
        }

        while (phone.length() < 7) phone.append('_');

        if (phone.length() == 7 || phone.charAt(7) != '-') {
            phone.insert(7, '-');
            if (caret > 7 || (caret == 7 && end <= 7 && change.isDeleted())) caret++;
            if (anchor > 7 || (anchor == 7 && end <= 7 && change.isDeleted())) anchor++;
        }

        while (phone.length() < 10) phone.append('_');

        if (phone.length() == 10 || phone.charAt(10) != '-') {
            phone.insert(10, '-');
            if (caret > 10 || (caret == 10 && end <= 10 && change.isDeleted())) caret++;
            if (anchor > 10 || (anchor == 10 && end <= 10 && change.isDeleted())) anchor++;
        }

        while (phone.length() < 13) phone.append('_');

        if (phone.length() > 13) phone.delete(13, phone.length());

        text = phone.toString();
        anchor = Math.min(anchor, 13);
        caret = Math.min(caret, 13);

        change.setText(text);
        change.setRange(0, change.getControlText().length());
        change.setAnchor(anchor);
        change.setCaretPosition(caret);

        return change;
    }
}