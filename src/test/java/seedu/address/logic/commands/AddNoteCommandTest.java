package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Note;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code AddNoteCommand}.
 */
public class AddNoteCommandTest {

    private static final String NOTE_STUB = "Prefers Telegram, loves Nasi Lemak";

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_addNoteExistingPerson_success() throws Exception {
        Name targetName = new Name("Alice Pauline");
        Note note = new Note(NOTE_STUB);

        Person personToEdit = model.getFilteredPersonList().stream()
            .filter(p -> p.getName().equals(targetName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Person not found in typical persons"));

        Person editedPerson = new PersonBuilder(personToEdit).withCulture(NOTE_STUB).build();

        AddNoteCommand command = new AddNoteCommand(targetName, note);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_ADD_NOTE_SUCCESS, editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteNoteExistingPerson_success() throws Exception {
        Name targetName = new Name("Alice Pauline");
        Note emptyNote = new Note("");

        Person personToEdit = model.getFilteredPersonList().stream()
            .filter(p -> p.getName().equals(targetName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Person not found"));

        Person editedPerson = new PersonBuilder(personToEdit).withCulture("").build();

        AddNoteCommand command = new AddNoteCommand(targetName, emptyNote);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS, editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personNotFound_failure() {
        Name invalidName = new Name("Nonexistent Person");
        Note note = new Note("Some note");
        AddNoteCommand command = new AddNoteCommand(invalidName, note);

        // execute_personNotFound_failure
        String expectedMessage = String.format(AddNoteCommand.MESSAGE_PERSON_NOT_FOUND, invalidName.fullName);
        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() {
        final AddNoteCommand standardCommand =
            new AddNoteCommand(new Name("Amy Bee"), new Note("Enjoys tea"));

        // same values -> returns true
        AddNoteCommand commandWithSameValues =
            new AddNoteCommand(new Name("Amy Bee"), new Note("Enjoys tea"));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different name -> returns false
        assertFalse(standardCommand.equals(
            new AddNoteCommand(new Name("Bob Choo"), new Note("Enjoys tea"))));

        // different note -> returns false
        assertFalse(standardCommand.equals(
            new AddNoteCommand(new Name("Amy Bee"), new Note("Loves hiking"))));
    }
}
