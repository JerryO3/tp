package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MONEY_OWED;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MoneyOwed;
import seedu.address.model.person.Person;

/**
 * Splits the sum of money owed among user and a group of people using the displayed
 * index from the address book.
 */
public class SplitCommand extends Command {
    public static final String COMMAND_WORD = "split";
    public static final Float MINIMUM_SPLIT_AMOUNT = (float) 0.01;
    public static final String MESSAGE_INVALID_AMOUNT =
            "Amount after splitting should be more than $0.01!";
    public static final String MESSAGE_MISSING_AMOUNT =
            "Please enter an amount that you want to split!";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Splits the sum of money owed among you and a group of people "
            + "using the displayed index from the address book.\n"
            + "Maximum amount you can split is $100,000."
            + "Parameters: at least one INDEX (must be a positive integer) "
            + PREFIX_MONEY_OWED + "MONEY_OWED "
            + "Example: " + COMMAND_WORD + " 1 2 "
            + PREFIX_MONEY_OWED + "4.50";
    private final List<Index> indexListToSplit;
    private final MoneyOwed totalOwed;

    /**
     * Returns a new SplitCommand object that takes in a list of index
     * and a MoneyOwed object.
     *
     * @param indexListToSplit list of index of person to split with.
     * @param totalOwed total amount to split.
     */
    public SplitCommand(List<Index> indexListToSplit, MoneyOwed totalOwed) {
        this.indexListToSplit = indexListToSplit;
        this.totalOwed = totalOwed;
    }

    /**
     * Splits the total amount of a group of people.
     *
     * @param totalAmount total amount to split.
     * @param numPeople number of people to split with.
     * @return the split amount
     */
    public static Float getSplitAmount(Float totalAmount, int numPeople) {
        String splitAmountRounded = String.format("%.2f", totalAmount / numPeople);
        Float splitAmount = Float.parseFloat(splitAmountRounded);
        return splitAmount;
    }

    /**
     * Checks if the index list is valid.
     *
     * @param indexList list of index of person to split with.
     * @param sizeOfLastShownList size of last displayed list.
     * @return true if each index in index list is valid.
     */
    public static boolean hasValidIndexList(List<Index> indexList, int sizeOfLastShownList) {
        for (Index index : indexList) {
            if (index.getZeroBased() >= sizeOfLastShownList) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all person in the split does not exceed the minimum and maximum amount.
     *
     * @param lastShownList list of person in the display list.
     * @param indexList list of index to split amount with.
     * @param splitAmount amount to be added to each person.
     * @return true if each person money owed does not exceed limit after split.
     */
    public static boolean hasValidAmountAfterSplit(List<Person> lastShownList, List<Index> indexList,
                                                   Float splitAmount) {
        try {
            for (Index index : indexList) {
                Person person = lastShownList.get(index.getZeroBased());
                person.getMoneyOwed().addAmountOwed(splitAmount);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        Float splitAmount = getSplitAmount(totalOwed.getAmount(), indexListToSplit.size() + 1);

        if (splitAmount < MINIMUM_SPLIT_AMOUNT) {
            throw new CommandException(MESSAGE_INVALID_AMOUNT);
        }
        if (!hasValidIndexList(indexListToSplit, lastShownList.size())) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        if (!hasValidAmountAfterSplit(lastShownList, indexListToSplit, splitAmount)) {
            throw new CommandException(MoneyOwed.MESSAGE_CONSTRAINTS);
        }

        for (Index index : indexListToSplit) {
            Person personToEdit = lastShownList.get(index.getZeroBased());
            Person editedPerson = new Person(
                    personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                    personToEdit.getAddress(), personToEdit.getRemark(), personToEdit.getTags(),
                    personToEdit.getBirthday(), personToEdit.getMoneyOwed().addAmountOwed(splitAmount),
                    personToEdit.getDaysAvailable());

            model.setPerson(personToEdit, editedPerson);
        }
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(
                String.format("$%s has been split among you and %d more people!",
                        totalOwed, indexListToSplit.size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SplitCommand)) {
            return false;
        }

        SplitCommand otherSplitCommand = (SplitCommand) other;
        return indexListToSplit.equals(otherSplitCommand.indexListToSplit)
                && totalOwed.equals(otherSplitCommand.totalOwed);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("indexListToSplit", indexListToSplit)
                .add("totalOwed", totalOwed)
                .toString();
    }
}
