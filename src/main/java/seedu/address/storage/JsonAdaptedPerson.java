package seedu.address.storage;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.AddedOn;
import seedu.address.model.person.Address;
import seedu.address.model.person.Country;
import seedu.address.model.person.Email;
import seedu.address.model.person.Event;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Offset;
import seedu.address.model.person.Organisation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PreferredLanguage;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String country;
    private final String event;
    private final String note;
    private final String offset;
    private final String organisation;
    private final boolean archivalStatus;
    private final String addedOn;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private String preferredLanguage;
    private final String preferredChannel;


    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
                             @JsonProperty("email") String email, @JsonProperty("address") String address,
                             @JsonProperty("country") String country, @JsonProperty("note") String note,
                             @JsonProperty("organisation") String organisation,
                             @JsonProperty("event") String event,
                             @JsonProperty("offset") String offset,
                             @JsonProperty("tags") List<JsonAdaptedTag> tags,
                             @JsonProperty("preferredLanguage") String preferredLanguage,
                             @JsonProperty("preferredChannel") String preferredChannel,
                             @JsonProperty("addedOn") String addedOn,
                             @JsonProperty("archivalStatus") boolean archivalStatus) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.country = country;
        this.organisation = organisation;
        this.event = event;
        this.note = note;
        this.offset = offset;
        this.preferredLanguage = preferredLanguage;
        this.preferredChannel = preferredChannel;
        this.addedOn = addedOn;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.archivalStatus = archivalStatus;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        country = source.getCountry() != null ? source.getCountry().toString() : null;
        organisation = source.getOrganisation().value;
        event = source.getEvent().value;
        note = source.getNote().value;
        offset = source.getOffset().value;
        preferredLanguage =
                source.getPreferredLanguage() == null ? null : source.getPreferredLanguage().getPreferredLanguage();
        preferredChannel = source.getPreferredChannel() == null
                ? null
                : source.getPreferredChannel().toString();
        addedOn = source.getAddedOn() == null ? null : source.getAddedOn().toString();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        archivalStatus = source.getArchivalStatus();
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's
     * {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in
     *                               the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }

        final Address modelAddress = new Address(address);

        final Organisation modelOrganisation = (organisation == null) ? new Organisation("")
            : new Organisation(organisation);

        final Event modelEvent = (event == null) ? new Event("") : new Event(event);

        final Note modelNote = (note == null) ? new Note("") : new Note(note);

        final Offset modelOffset;
        if (offset == null || offset.isEmpty()) {
            modelOffset = new Offset("+00:00"); // default UTC
        } else {
            try {
                modelOffset = new Offset(offset);
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException("Invalid GMT offset: " + offset
                        + ". Must be in the format +HH:MM or -HH:MM, where HH is 00-14 and MM is 00-59.");
            }
        }

        AddedOn modelAddedOn;
        if (addedOn == null || addedOn.isBlank()) {
            modelAddedOn = new AddedOn(LocalDateTime.now());
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (HH:mm)", Locale.ENGLISH);
                modelAddedOn = new AddedOn(LocalDateTime.parse(addedOn, formatter));
            } catch (DateTimeParseException e) {
                modelAddedOn = new AddedOn(LocalDateTime.now());
            }
        }

        if (!isNull(country) && !Country.isValidCountry(country)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Country modelCountry = isNull(country) ? null : new Country(country);

        if (isNull(preferredLanguage)) {
            preferredLanguage = "english";
        }
        if (!PreferredLanguage.isValidLanguage(preferredLanguage)) {
            throw new IllegalValueException(PreferredLanguage.MESSAGE_CONSTRAINTS);
        }
        final PreferredLanguage modelPreferredLanguage = new PreferredLanguage(preferredLanguage);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        final Person.CommunicationChannel modelChannel;
        if (preferredChannel == null || preferredChannel.isBlank()) {
            modelChannel = null;
        } else {
            try {
                modelChannel = Person.CommunicationChannel.valueOf(preferredChannel.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException("Invalid communication channel: " + preferredChannel);
            }
        }

        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelCountry, modelOrganisation,
            modelEvent, modelNote, modelChannel, modelTags, modelOffset, modelPreferredLanguage, modelAddedOn,
                archivalStatus);
    }

}
