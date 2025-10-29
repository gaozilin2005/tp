package seedu.address.model.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Country;
import seedu.address.model.person.Email;
import seedu.address.model.person.Event;
import seedu.address.model.person.AddedOn;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Offset;
import seedu.address.model.person.Organisation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PreferredLanguage;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    public static final Note EMPTY_NOTE = new Note("");
    public static final Country EMPTY_COUNTRY = new Country("");

    public static Person[] getSamplePersons() {
        return new Person[]{
            new Person(new Name("Alex Yeoh"), new Phone("+6587438807"), new Email("alexyeoh@example.com"),
                    new Address("Blk 30 Geylang Street 29, #06-40"), new Country("Singapore"),
                    new Organisation("National University of Singapore (NUS)"), new Event("University Assembly"),
                    new Note("follows Chinese Traditions"), Person.CommunicationChannel.EMAIL,
                    getTagSet("friends"), new Offset("+08:00"), new PreferredLanguage("english"),
                    new AddedOn(LocalDateTime.now()), false)
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
