package edu.westga.comp2320.studymate;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HelloController {

    private final ToggleGroup dayOfWeekToggleGroup = new ToggleGroup();

    @FXML
    private RadioButton mondayRadioButton;

    @FXML
    private RadioButton tuesdayRadioButton;

    @FXML
    private RadioButton wednesdayRadioButton;

    @FXML
    private RadioButton thursdayRadioButton;

    @FXML
    private RadioButton fridayRadioButton;

    @FXML
    private Label dayOfWeekErrorLabel;

    @FXML
    private CheckBox englCheckBox;

    @FXML
    private CheckBox histCheckBox;

    @FXML
    private CheckBox mathCheckBox;

    @FXML
    private CheckBox compCheckBox;

    @FXML
    private Label subjectErrorLabel;

    @FXML
    private TextField taskTextField;

    @FXML
    private ListView<StudySession> studySessionsListView;

    @FXML
    private void initialize() {
        this.configureDayOfWeekRadioButtons();
        this.configureSubjectCheckBoxes();
        this.configureStudySessionListViewDisplay();

        this.studySessionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                this.populateTextFields(newValue));

        this.dayOfWeekToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.dayOfWeekErrorLabel.setText("");
            }
        });
    }

    private void configureDayOfWeekRadioButtons() {
        this.configureDayRadioButton(this.mondayRadioButton, "M");
        this.configureDayRadioButton(this.tuesdayRadioButton, "T");
        this.configureDayRadioButton(this.wednesdayRadioButton, "W");
        this.configureDayRadioButton(this.thursdayRadioButton, "R");
        this.configureDayRadioButton(this.fridayRadioButton, "F");
    }

    private void configureDayRadioButton(RadioButton radioButton, String dayCode) {
        radioButton.setToggleGroup(this.dayOfWeekToggleGroup);
        radioButton.setUserData(dayCode);
    }

    private void configureSubjectCheckBoxes() {
        this.englCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> this.clearSubjectErrorIfValid());
        this.histCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> this.clearSubjectErrorIfValid());
        this.mathCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> this.clearSubjectErrorIfValid());
        this.compCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> this.clearSubjectErrorIfValid());
    }

    private void configureStudySessionListViewDisplay() {
        this.studySessionsListView.setCellFactory((listView) -> new ListCell<>() {
            @Override
            protected void updateItem(StudySession session, boolean empty) {
                super.updateItem(session, empty);
                if (empty || session == null) {
                    this.setText(null);
                    return;
                }

                this.setText(HelloController.this.getListViewDisplayText(session, this.getIndex()));
            }
        });
    }

    private String getListViewDisplayText(StudySession session, int index) {
        StringBuilder text = new StringBuilder();
        if (this.isFirstSessionForDay(index, session.getDayOfWeek())) {
            if (index > 0) {
                text.append("\n");
            }
            text.append(this.getDayName(session.getDayOfWeek())).append("\n");
        }

        text.append(this.getSubjectTaskDisplayText(session));
        return text.toString();
    }

    private boolean isFirstSessionForDay(int index, String dayCode) {
        if (index <= 0) {
            return true;
        }

        if (index >= this.studySessionsListView.getItems().size()) {
            return false;
        }

        StudySession previousSession = this.studySessionsListView.getItems().get(index - 1);
        return !dayCode.equals(previousSession.getDayOfWeek());
    }

    private String getSubjectTaskDisplayText(StudySession session) {
        String taskSuffix = "";
        if (session.getTask() != null && !session.getTask().isBlank()) {
            taskSuffix = " – " + session.getTask();
        }

        String[] subjects = session.getSubject().split(",");
        StringBuilder subjectLines = new StringBuilder();
        for (String subject : subjects) {
            String trimmedSubject = subject.trim();
            if (trimmedSubject.isEmpty()) {
                continue;
            }

            if (subjectLines.length() > 0) {
                subjectLines.append("\n");
            }
            subjectLines.append(trimmedSubject).append(taskSuffix);
        }

        if (subjectLines.length() == 0) {
            return session.getSubject() + taskSuffix;
        }

        return subjectLines.toString();
    }

    private void clearSubjectErrorIfValid() {
        if (!this.getSelectedSubjects().isEmpty()) {
            this.subjectErrorLabel.setText("");
        }
    }

    @FXML
    protected void onAddButtonClick() {
        this.clearErrors();

        boolean hasError = false;
        String dayOfWeek = this.getSelectedDayCode();
        if (dayOfWeek == null) {
            this.dayOfWeekErrorLabel.setText("must select M, T, W, R, or F");
            hasError = true;
        }

        List<String> subjects = this.getSelectedSubjects();
        if (subjects.isEmpty()) {
            this.subjectErrorLabel.setText("select at least one subject");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        String subjectText = String.join(", ", subjects);
        StudySession session = new StudySession(dayOfWeek, subjectText, this.taskTextField.getText());
        this.studySessionsListView.getItems().add(session);
        this.sortStudySessions();
        this.studySessionsListView.refresh();
        this.studySessionsListView.getSelectionModel().select(session);
    }

    @FXML
    protected void onDeleteButtonClick() {
        int selectedIndex = this.studySessionsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        this.studySessionsListView.getItems().remove(selectedIndex);
        if (this.studySessionsListView.getItems().isEmpty()) {
            this.studySessionsListView.refresh();
            return;
        }

        int indexToSelect = Math.min(selectedIndex, this.studySessionsListView.getItems().size() - 1);
        this.studySessionsListView.refresh();
        this.studySessionsListView.getSelectionModel().select(indexToSelect);
    }

    @FXML
    protected void onSaveMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Study Sessions");
        fileChooser.setInitialFileName("study-sessions.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Window window = this.studySessionsListView.getScene() == null ? null : this.studySessionsListView.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(window);
        if (selectedFile == null) {
            return;
        }

        this.saveSessionsToFile(selectedFile);
    }

    private void saveSessionsToFile(File file) {
        List<String> lines = new ArrayList<>();
        lines.add("dayOfWeek,subject,task");

        for (StudySession session : this.studySessionsListView.getItems()) {
            String csvLine = this.toCsvValue(session.getDayOfWeek())
                    + "," + this.toCsvValue(session.getSubject())
                    + "," + this.toCsvValue(session.getTask());
            lines.add(csvLine);
        }

        try {
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            this.subjectErrorLabel.setText("save failed");
        }
    }

    private String toCsvValue(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    private String getDayName(String dayCode) {
        return switch (dayCode) {
            case "M" -> "Monday";
            case "T" -> "Tuesday";
            case "W" -> "Wednesday";
            case "R" -> "Thursday";
            case "F" -> "Friday";
            default -> dayCode;
        };
    }

    private void clearErrors() {
        this.dayOfWeekErrorLabel.setText("");
        this.subjectErrorLabel.setText("");
    }

    private void populateTextFields(StudySession session) {
        if (session == null) {
            this.dayOfWeekToggleGroup.selectToggle(null);
            this.clearSubjectSelections();
            this.taskTextField.setText("");
            return;
        }

        this.selectDayOfWeekToggle(session.getDayOfWeek());
        this.selectSubjectsFromText(session.getSubject());
        this.taskTextField.setText(session.getTask() == null ? "" : session.getTask());
    }

    private List<String> getSelectedSubjects() {
        List<String> selectedSubjects = new ArrayList<>();

        if (this.englCheckBox.isSelected()) {
            selectedSubjects.add("ENGL");
        }
        if (this.histCheckBox.isSelected()) {
            selectedSubjects.add("HIST");
        }
        if (this.mathCheckBox.isSelected()) {
            selectedSubjects.add("MATH");
        }
        if (this.compCheckBox.isSelected()) {
            selectedSubjects.add("COMP");
        }

        selectedSubjects.sort(String.CASE_INSENSITIVE_ORDER);
        return selectedSubjects;
    }

    private void clearSubjectSelections() {
        this.englCheckBox.setSelected(false);
        this.histCheckBox.setSelected(false);
        this.mathCheckBox.setSelected(false);
        this.compCheckBox.setSelected(false);
    }

    private void selectSubjectsFromText(String subjectText) {
        this.clearSubjectSelections();
        if (subjectText == null || subjectText.isBlank()) {
            return;
        }

        String[] subjects = subjectText.split(",");
        for (String subject : subjects) {
            String normalized = subject.trim().toUpperCase();
            switch (normalized) {
                case "ENGL" -> this.englCheckBox.setSelected(true);
                case "HIST" -> this.histCheckBox.setSelected(true);
                case "MATH" -> this.mathCheckBox.setSelected(true);
                case "COMP" -> this.compCheckBox.setSelected(true);
                default -> {
                }
            }
        }
    }

    private String getSelectedDayCode() {
        Toggle selectedToggle = this.dayOfWeekToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            return null;
        }

        return selectedToggle.getUserData().toString();
    }

    private void selectDayOfWeekToggle(String dayOfWeek) {
        for (Toggle toggle : this.dayOfWeekToggleGroup.getToggles()) {
            if (dayOfWeek.equals(toggle.getUserData())) {
                this.dayOfWeekToggleGroup.selectToggle(toggle);
                return;
            }
        }

        this.dayOfWeekToggleGroup.selectToggle(null);
    }

    private void sortStudySessions() {
        this.studySessionsListView.getItems().sort(
                Comparator.comparingInt((StudySession session) -> this.getDaySortOrder(session.getDayOfWeek()))
                        .thenComparing(StudySession::getSubject, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(StudySession::getSubject)
        );
    }

    private int getDaySortOrder(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "M" -> 1;
            case "T" -> 2;
            case "W" -> 3;
            case "R" -> 4;
            case "F" -> 5;
            default -> Integer.MAX_VALUE;
        };
    }
}
