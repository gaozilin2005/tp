package seedu.address.model.person;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents the date the user met a contact for the first time.
 */
public class MetOn implements Comparable<MetOn> {

    public final LocalDateTime localDateTime;

    public MetOn(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public int compareTo(MetOn other) {
        return this.localDateTime.compareTo(other.localDateTime);
    }

    /**
     * Returns a string representation of the time.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (HH:mm)", Locale.ENGLISH);
        return localDateTime.format(formatter);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MetOn o)) {
            return false;
        }
        return localDateTime.equals(o.localDateTime);
    }

    @Override
    public int hashCode() {
        return localDateTime.hashCode();
    }
}
