package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CultureTest {

    @Test
    public void equals() {
        Note culture = new Note("Hello");

        // same object -> returns true
        assertTrue(culture.equals(culture));

        // same values -> returns true
        Note cultureCopy = new Note(culture.value);
        assertTrue(culture.equals(cultureCopy));

        // different types -> returns false
        assertFalse(culture.equals(1));

        // null -> returns false
        assertFalse(culture.equals(null));

        // different remark -> returns false
        Note differentRemark = new Note("Bye");
        assertFalse(culture.equals(differentRemark));
    }
}
