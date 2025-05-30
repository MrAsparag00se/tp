package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddHouseholdCommand;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteHouseholdCommand;
import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.logic.commands.EditHouseholdCommand;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindHouseholdCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListHouseholdsCommand;
import seedu.address.logic.commands.ViewFullSessionCommand;
import seedu.address.logic.commands.ViewHouseholdSessionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
/**
 * Parses user input for the household management system.
 */
public class HouseholdBookParser {
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private final Model model;

    public HouseholdBookParser(Model model) {
        this.model = model;
    }
    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case AddHouseholdCommand.COMMAND_WORD -> {
            return new AddHouseholdCommandParser().parse(arguments);
        }
        case AddSessionCommand.COMMAND_WORD -> {
            return new AddSessionCommandParser().parse(arguments);
        }
        case EditHouseholdCommand.COMMAND_WORD -> {
            return new EditHouseholdCommandParser(model).parse(arguments);
        }
        case EditSessionCommand.COMMAND_WORD -> {
            return new EditSessionCommandParser(model).parse(arguments);
        }
        case DeleteHouseholdCommand.COMMAND_WORD -> {
            return new DeleteHouseholdCommandParser().parse(arguments);
        }
        case DeleteSessionCommand.COMMAND_WORD -> {
            return new DeleteSessionCommandParser().parse(arguments);
        }
        case ClearCommand.COMMAND_WORD -> {
            return new ClearCommand();
        }
        case FindHouseholdCommand.COMMAND_WORD -> {
            return new FindHouseholdCommandParser().parse(arguments);
        }
        case ListHouseholdsCommand.COMMAND_WORD -> {
            return new ListHouseholdsCommand();
        }
        case ViewHouseholdSessionsCommand.COMMAND_WORD -> {
            return new ViewHouseholdSessionsCommandParser().parse(arguments);
        }
        case ExitCommand.COMMAND_WORD -> {
            return new ExitCommand();
        }
        case HelpCommand.COMMAND_WORD -> {
            return new HelpCommand();
        }
        case ViewFullSessionCommand.COMMAND_WORD -> {
            return new ViewFullSessionCommandParser().parse(arguments);
        }
        default -> throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
