package edu.westga.comp2320.studymate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudySessionTest {

    @Test
    void constructorWithoutTaskStoresNormalizedValues() {
        StudySession session = new StudySession(" t ", "  COMP2320  ");

        assertEquals("T", session.getDayOfWeek());
        assertEquals("COMP2320", session.getSubject());
        assertNull(session.getTask());
    }

    @Test
    void constructorWithTaskStoresTrimmedTask() {
        StudySession session = new StudySession("M", "English", "  chapter 3 notes  ");

        assertEquals("chapter 3 notes", session.getTask());
    }

    @Test
    void constructorWithBlankTaskTreatsTaskAsMissing() {
        StudySession session = new StudySession("W", "HIST", "   ");

        assertNull(session.getTask());
    }

    @Test
    void constructorWithInvalidDayThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession("S", "COMP2320"));
    }

    @Test
    void constructorWithNullDayThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession(null, "COMP2320"));
    }

    @Test
    void constructorWithNullSubjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession("M", null));
    }

    @Test
    void constructorWithBlankSubjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession("M", "   "));
    }

    @Test
    void toStringWithTaskIncludesDaySubjectAndTask() {
        StudySession session = new StudySession("R", "HIST", "homework");

        assertEquals("Thursday: HIST - homework", session.toString());
    }

    @Test
    void toStringWithoutTaskIncludesOnlyDayAndSubject() {
        StudySession session = new StudySession("T", "COMP2200");

        assertEquals("Tuesday: COMP2200", session.toString());
    }
}
