package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

    @Test
    public void isValidTag_exceedsMaxLength_returnsFalse() {
        String longTag = "a".repeat(31);
        assertFalse(Tag.isValidTagName(longTag));
    }

    @Test
    public void isValidTag_maxLengthBoundary_returnsTrue() {
        String validTag = "a".repeat(30);
        assertTrue(Tag.isValidTagName(validTag));
    }

}
