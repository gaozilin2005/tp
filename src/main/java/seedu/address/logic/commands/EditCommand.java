package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CHANNEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COUNTRY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EVENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LANGUAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OFFSET;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORGANISATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_UNARCHIVED;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
import seedu.address.model.person.Person.CommunicationChannel;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PreferredLanguage;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_OFFSET + "OFFSET] "
            + "[" + PREFIX_COUNTRY + "COUNTRY] "
            + "[" + PREFIX_ORGANISATION + "ORGANISATION] "
            + "[" + PREFIX_EVENT + "EVENT] "
            + "[" + PREFIX_CHANNEL + " CHANNEL] "
            + "[" + PREFIX_LANGUAGE + " LANGUAGE] "
            + "[" + PREFIX_NOTE + "NOTE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index                of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Country updatedCountry = editPersonDescriptor.getCountry().orElse(personToEdit.getCountry());
        Organisation updatedOrganisation = editPersonDescriptor.getOrganisation()
            .orElse(personToEdit.getOrganisation());
        Event updatedEvent = editPersonDescriptor.getEvent().orElse(personToEdit.getEvent());
        Note updatedNote = editPersonDescriptor.getNote().orElse(personToEdit.getNote());
        Offset updatedOffset = editPersonDescriptor.getGmtOffset().orElse(personToEdit.getOffset());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        CommunicationChannel updatedChannel = editPersonDescriptor.getChannel()
                .orElse(personToEdit.getPreferredChannel());
        PreferredLanguage updatedLang =
                editPersonDescriptor.getPreferredLanguage().orElse(personToEdit.getPreferredLanguage());
        boolean isArchived = personToEdit.getArchivalStatus();
        AddedOn updatedAddedOn = personToEdit.getAddedOn();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedCountry, updatedOrganisation,
            updatedEvent, updatedNote, updatedChannel, updatedTags, updatedOffset, updatedLang, updatedAddedOn,
            isArchived);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_UNARCHIVED);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will
     * replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Country country;
        private Organisation organisation;
        private Event event;
        private Note note;
        private Set<Tag> tags;
        private CommunicationChannel channel;
        private Offset offset;
        private PreferredLanguage preferredLanguage;
        private AddedOn addedOn;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setCountry(toCopy.country);
            setEvent(toCopy.event);
            setNote(toCopy.note);
            setOrganisation(toCopy.organisation);
            setTags(toCopy.tags);
            setChannel(toCopy.channel);
            setOffset(toCopy.offset);
            setPreferredLanguage(toCopy.preferredLanguage);
            setAddedOn(toCopy.addedOn);
        }

        public void setChannel(CommunicationChannel channel) {
            this.channel = channel;
        }

        public Optional<CommunicationChannel> getChannel() {
            return Optional.ofNullable(channel);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, offset, country, organisation, event,
                                               channel, preferredLanguage, note, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public void setAddedOn(AddedOn addedOn) {
            this.addedOn = addedOn;
        }

        public Optional<AddedOn> getAddedOn() {
            return Optional.ofNullable(addedOn);
        }

        public Optional<Country> getCountry() {
            return Optional.ofNullable(country);
        }

        public Optional<Organisation> getOrganisation() {
            return Optional.ofNullable(organisation);
        }
        public Optional<Event> getEvent() {
            return Optional.ofNullable(event);
        }
        public Optional<Note> getNote() {
            return Optional.ofNullable(note);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setOffset(Offset offset) {
            this.offset = offset;
        }

        public Optional<Offset> getGmtOffset() {
            return Optional.ofNullable(offset);
        }

        public void setPreferredLanguage(PreferredLanguage preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
        }

        public void setOrganisation(Organisation organisation) {
            this.organisation = organisation;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public void setNote(Note note) {
            this.note = note;
        }

        public Optional<PreferredLanguage> getPreferredLanguage() {
            return Optional.ofNullable(preferredLanguage);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(country, otherEditPersonDescriptor.country)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(channel, otherEditPersonDescriptor.channel)
                    && Objects.equals(event, otherEditPersonDescriptor.event)
                    && Objects.equals(note, otherEditPersonDescriptor.note)
                    && Objects.equals(organisation, otherEditPersonDescriptor.organisation)
                    && Objects.equals(offset, otherEditPersonDescriptor.offset)
                    && Objects.equals(preferredLanguage, otherEditPersonDescriptor.preferredLanguage);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("country", country)
                .add("organisation", organisation)
                .add("event", event)
                .add("note", note)
                .add("tags", tags)
                .add("channel", channel)
                .add("offset", offset)
                .add("language", preferredLanguage)
                .add("addedOn", addedOn)
                .toString();
        }
    }
}
