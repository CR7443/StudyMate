package edu.westga.comp2320.studymate.model;

public class StudySession {

    private final String dayOfWeek;
    private final String subject;
    private final String task;

    public StudySession(String dayOfWeek, String subject) {
        this(dayOfWeek, subject, null);
    }

    public StudySession(String dayOfWeek, String subject, String task) {
        this.dayOfWeek = this.validateDayOfWeek(dayOfWeek);
        this.subject = this.validateSubject(subject);
        this.task = this.normalizeTask(task);
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getTask() {
        return this.task;
    }

    @Override
    public String toString() {
        String result = this.getDayOfWeekName() + ": " + this.subject;
        if (this.task != null) {
            result += " - " + this.task;
        }
        return result;
    }

    private String getDayOfWeekName() {
        return switch (this.dayOfWeek) {
            case "M" -> "Monday";
            case "T" -> "Tuesday";
            case "W" -> "Wednesday";
            case "R" -> "Thursday";
            case "F" -> "Friday";
            default -> throw new IllegalStateException("Unexpected day code: " + this.dayOfWeek);
        };
    }

    private String validateDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("dayOfWeek must not be null");
        }

        String normalizedDay = dayOfWeek.trim().toUpperCase();
        if (!normalizedDay.equals("M")
                && !normalizedDay.equals("T")
                && !normalizedDay.equals("W")
                && !normalizedDay.equals("R")
                && !normalizedDay.equals("F")) {
            throw new IllegalArgumentException("dayOfWeek must be one of M, T, W, R, or F");
        }

        return normalizedDay;
    }

    private String validateSubject(String subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null or blank");
        }

        String normalizedSubject = subject.trim();
        if (normalizedSubject.isEmpty()) {
            throw new IllegalArgumentException("subject must not be null or blank");
        }

        return normalizedSubject;
    }

    private String normalizeTask(String task) {
        if (task == null) {
            return null;
        }

        String normalizedTask = task.trim();
        if (normalizedTask.isEmpty()) {
            return null;
        }

        return normalizedTask;
    }
}
