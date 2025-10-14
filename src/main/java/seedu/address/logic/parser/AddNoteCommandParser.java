package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Note;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates a new {@code AddNoteCommand} object.
 *
 * <p>Expected format: {@code addnote name: &lt;name&gt; note: &lt;note text&gt;}</p>
 */
public class AddNoteCommandParser implements Parser<AddNoteCommand> {

    /**
     * Regex to capture:
     *  - name: everything before the next " note:" (non-greedy)
     *  - note: rest of the line
     *
     * Example matches:
     *   "name: John Doe note: Prefers Telegram"
     *   "name: Alice B.  note: Loves Nasi Lemak and durian."
     */
    private static final Pattern ADDNOTE_COMMAND_FORMAT =
        Pattern.compile("(?i)name:\\s*(?<name>.*?)\\s+note:\\s*(?<note>.*)");

    @Override
    public AddNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        final Matcher matcher = ADDNOTE_COMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddNoteCommand.MESSAGE_USAGE));
        }

        String nameStr = matcher.group("name").trim();
        String noteStr = matcher.group("note").trim();

        Name name = ParserUtil.parseName(nameStr);
        Note culture = new Note(noteStr);

        return new AddNoteCommand(name, culture);
    }
}
