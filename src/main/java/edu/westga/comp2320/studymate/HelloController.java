package edu.westga.comp2320.studymate;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.util.Comparator;

public class HelloController {

    @FXML
    private ToggleGroup dayOfWeekToggleGroup;

    @FXML
    private Label dayOfWeekErrorLabel;

    @FXML
    private TextField subjectTextField;

    @FXML
    private Label subjectErrorLabel;

    @FXML
    private TextField taskTextField;

    @FXML
    private ListView<StudySession> studySessionsListView;

    @FXML
    private void initialize() {
        this.studySessionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                this.populateTextFields(newValue));

        this.dayOfWeekToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.dayOfWeekErrorLabel.setText("");
            }
        });
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

        String subject = this.subjectTextField.getText();
        if (subject == null || subject.trim().isEmpty()) {
            this.subjectErrorLabel.setText("required");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        StudySession session = new StudySession(dayOfWeek, subject, this.taskTextField.getText());
        this.studySessionsListView.getItems().add(session);
        this.sortStudySessions();
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
            return;
        }

        int indexToSelect = Math.min(selectedIndex, this.studySessionsListView.getItems().size() - 1);
        this.studySessionsListView.getSelectionModel().select(indexToSelect);
    }

    private void clearErrors() {
        this.dayOfWeekErrorLabel.setText("");
        this.subjectErrorLabel.setText("");
    }

    private void populateTextFields(StudySession session) {
        if (session == null) {
            this.dayOfWeekToggleGroup.selectToggle(null);
            this.subjectTextField.setText("");
            this.taskTextField.setText("");
            return;
        }

        this.selectDayOfWeekToggle(session.getDayOfWeek());
        this.subjectTextField.setText(session.getSubject());
        this.taskTextField.setText(session.getTask() == null ? "" : session.getTask());
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
