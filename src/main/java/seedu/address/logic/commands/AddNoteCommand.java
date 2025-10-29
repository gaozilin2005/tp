package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_UNARCHIVED;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

/**
 * Adds or updates a cultural note for a person identified by name.
 */
public class AddNoteCommand extends Command {

    public static final String COMMAND_WORD = "addnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds or updates a note for the specified person. "
            + "Parameters: name: <name> note: <note text>\n"
            + "Example: " + COMMAND_WORD
            + " name: John Doe note: Prefers to communicate on Telegram, loves Nasi Lemak.";

    public static final String MESSAGE_ADD_NOTE_SUCCESS = "Added note to Person: %1$s";
    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Removed note from Person: %1$s";
    public static final String MESSAGE_PERSON_NOT_FOUND = "No person found with name: %1$s";

    private final Name name;
    private final Note note; // “Note” here represents the note text

    /**
     * Creates an AddNoteCommand to add or update the given person's note.
     */
    public AddNoteCommand(Name name, Note note) {
        requireAllNonNull(name, note);
        this.name = name;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> allPersons = model.getAddressBook().getPersonList();

        // Find the person with the matching name
        Person personToEdit = allPersons.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (personToEdit == null) {
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, name.fullName));
        }

        // Create edited person with updated note
        Person editedPerson = new Person(
            personToEdit.getName(),
            personToEdit.getPhone(),
            personToEdit.getEmail(),
            personToEdit.getAddress(),
            personToEdit.getCountry(),
            personToEdit.getOrganisation(),
            personToEdit.getEvent(),
            note,
            personToEdit.getTags(),
            personToEdit.getOffset(),
            personToEdit.getPreferredLanguage(),
            personToEdit.getAddedOn(),
            personToEdit.getArchivalStatus()
        );

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_UNARCHIVED);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether the note is
     * added or removed.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !note.value.isEmpty()
                ? MESSAGE_ADD_NOTE_SUCCESS
                : MESSAGE_DELETE_NOTE_SUCCESS;
        return String.format(message, personToEdit.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddNoteCommand
                        && name.equals(((AddNoteCommand) other).name)
                        && note.equals(((AddNoteCommand) other).note));
    }
}
