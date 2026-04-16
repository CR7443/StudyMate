package edu.westga.comp2320.studymate;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private TextField dayOfWeekTextField;

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
    }

    @FXML
    protected void onAddButtonClick() {
        this.clearErrors();

        boolean hasError = false;
        String dayOfWeek = this.dayOfWeekTextField.getText();
        if (!this.isValidDayOfWeek(dayOfWeek)) {
            this.dayOfWeekErrorLabel.setText("must be M, T, W, R, or F");
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

    private boolean isValidDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null) {
            return false;
        }

        String normalizedDay = dayOfWeek.trim().toUpperCase();
        return normalizedDay.equals("M")
                || normalizedDay.equals("T")
                || normalizedDay.equals("W")
                || normalizedDay.equals("R")
                || normalizedDay.equals("F");
    }

    private void clearErrors() {
        this.dayOfWeekErrorLabel.setText("");
        this.subjectErrorLabel.setText("");
    }

    private void populateTextFields(StudySession session) {
        if (session == null) {
            this.dayOfWeekTextField.setText("");
            this.subjectTextField.setText("");
            this.taskTextField.setText("");
            return;
        }

        this.dayOfWeekTextField.setText(session.getDayOfWeek());
        this.subjectTextField.setText(session.getSubject());
        this.taskTextField.setText(session.getTask() == null ? "" : session.getTask());
    }
}
