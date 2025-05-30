package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.HouseholdBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.HouseholdBook;
import seedu.address.model.Model;
import seedu.address.model.household.Household;
import seedu.address.model.session.Session;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";
    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final HouseholdBookParser householdBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        householdBookParser = new HouseholdBookParser(model);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = householdBookParser.parseCommand(commandText);
        commandResult = command.execute(model);

        try {
            storage.saveHouseholdBook(model.getHouseholdBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public HouseholdBook getHouseholdBook() {
        return model.getHouseholdBook();
    }

    @Override
    public ObservableList<Household> getFilteredHouseholdList() {
        return model.getFilteredHouseholdList();
    }

    @Override
    public ObservableList<Session> getFilteredSessionList() {
        return model.getFilteredSessionList();
    }

    @Override
    public Path getHouseholdBookFilePath() {
        return model.getHouseholdBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public void updateFilteredSessionList(Predicate<Session> predicate) {
        model.updateFilteredSessionList(predicate);
    }

    @Override
    public void updateFilteredHouseholdList(Predicate<Household> predicate) {
        model.updateFilteredHouseholdList(predicate);
    }
}
