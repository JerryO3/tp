package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.JOHN;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.predicates.PersonHasTagPredicate;
import seedu.address.testutil.TestUtil;

public class FilterTagCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() throws Exception {
        PersonHasTagPredicate firstPredicate =
                new PersonHasTagPredicate(TestUtil.stringsToTags(Collections.singletonList("first")));
        PersonHasTagPredicate secondPredicate =
                new PersonHasTagPredicate(TestUtil.stringsToTags(Collections.singletonList("second")));

        FilterTagCommand filterFirstCommand = new FilterTagCommand(firstPredicate);
        FilterTagCommand filterSecondCommand = new FilterTagCommand(secondPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same values -> returns true
        FilterTagCommand findFirstCommandCopy = new FilterTagCommand(firstPredicate);
        assertTrue(filterFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        PersonHasTagPredicate predicate = preparePredicate(" ");
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneKeywords_onePersonFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PersonHasTagPredicate predicate = preparePredicate("TAs");
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Arrays.asList(JOHN), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneKeywords_multiplePersonsFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        PersonHasTagPredicate predicate = preparePredicate("friends");
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_onePersonFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PersonHasTagPredicate predicate = preparePredicate("Acquaintances TAs CCA");
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Arrays.asList(JOHN), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 4);
        PersonHasTagPredicate predicate = preparePredicate("friends TAs CCA");
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL, JOHN), model.getFilteredPersonList());
    }

    @Test
    public void execute_matchAllMultipleKeywords_personFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PersonHasTagPredicate predicate = preparePredicate("friends owesMoney", true);
        FilterTagCommand command = new FilterTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(
                command,
                model,
                new CommandResult(expectedMessage).withPersonToShow(Model.INVALID_PERSON_INDEX),
                expectedModel);
        assertEquals(Arrays.asList(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() throws Exception {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate(TestUtil.stringsToTags(Arrays.asList("keyword")));
        FilterTagCommand filterTagCommand = new FilterTagCommand(predicate);
        String expected = FilterTagCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, filterTagCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code PersonHasTagPredicate}.
     */
    private PersonHasTagPredicate preparePredicate(String userInput) throws Exception {
        return new PersonHasTagPredicate(TestUtil.stringsToTags(Arrays.asList(userInput.split("\\s+"))));
    }

    private PersonHasTagPredicate preparePredicate(String userInput, boolean matchAll) throws Exception {
        return new PersonHasTagPredicate(TestUtil.stringsToTags(Arrays.asList(userInput.split("\\s+"))), matchAll);
    }
}
