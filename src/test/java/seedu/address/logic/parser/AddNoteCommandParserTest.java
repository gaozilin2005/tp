package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.model.person.Note;
import seedu.address.model.person.Name;

public class AddNoteCommandParserTest {

    private static final String VALID_NAME = "Alice Pauline";
    private static final String VALID_NOTE = "Enjoys Nasi Lemak";
    private static final String EMPTY_NOTE = "";

    private final AddNoteCommandParser parser = new AddNoteCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = " " + PREFIX_NAME + VALID_NAME + " " + PREFIX_NOTE + VALID_NOTE;

        AddNoteCommand expectedCommand =
            new AddNoteCommand(new Name(VALID_NAME), new Note(VALID_NOTE));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_emptyNote_success() {
        String userInput = "name: Alice Pauline note: ";
        AddNoteCommand expectedCommand =
            new AddNoteCommand(new Name("Alice Pauline"), new Note(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddNoteCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, AddNoteCommand.COMMAND_WORD, expectedMessage);

        // no name
        assertParseFailure(parser, AddNoteCommand.COMMAND_WORD + " " + PREFIX_NOTE + VALID_NOTE, expectedMessage);

        // no note
        assertParseFailure(parser, AddNoteCommand.COMMAND_WORD + " " + PREFIX_NAME + VALID_NAME, expectedMessage);
    }
}
