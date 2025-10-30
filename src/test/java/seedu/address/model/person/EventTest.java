package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void isValidEvent_exceedsMaxLength_returnsFalse() {
        String longEvent = "a".repeat(101);
        assertFalse(Event.isValidEvent(longEvent));
    }

    @Test
    public void isValidEvent_maxLengthBoundary_returnsTrue() {
        String validEvent = "a".repeat(100);
        assertTrue(Event.isValidEvent(validEvent));
    }

    @Test
    public void equals() {
        Event event = new Event("Met at conference");

        // same object -> returns true
        assertTrue(event.equals(event));

        // same values -> returns true
        Event eventCopy = new Event("Met at conference");
        assertTrue(event.equals(eventCopy));

        // different types -> returns false
        assertFalse(event.equals(1));

        // null -> returns false
        assertFalse(event.equals(null));

        // different event -> returns false
        Event differentEvent = new Event("Team lunch");
        assertFalse(event.equals(differentEvent));

        // empty event vs non-empty -> returns false
        Event emptyEvent = new Event("");
        assertFalse(event.equals(emptyEvent));

        // two empty events -> returns true
        Event anotherEmpty = new Event("");
        assertTrue(emptyEvent.equals(anotherEmpty));
    }

    @Test
    public void toString_returnsValue() {
        Event event = new Event("NUS Career Fair 2024");
        assertEquals("NUS Career Fair 2024", event.toString());

        Event emptyEvent = new Event("");
        assertEquals("", emptyEvent.toString());
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        Event event1 = new Event("Project kickoff");
        Event event2 = new Event("Project kickoff");
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
