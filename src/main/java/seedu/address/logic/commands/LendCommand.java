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
 * Lends an amount of money on top of current amount to a person identified
 * using the displayed index from the address book.
 */
public class LendCommand extends Command {

    public static final String COMMAND_WORD = "lend";
    public static final String MESSAGE_LENT_PERSON_SUCCESS =
            "Lend to person %1$s";
    public static final String MESSAGE_MISSING_AMOUNT =
            "Please enter an amount that you want to lend!";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lend an amount of money on top of the current amount owed of a person "
            + "using the displayed index from the address book.\n"
            + "Maximum amount you can lend is $100,000.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_MONEY_OWED + "MONEY_OWED "
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_MONEY_OWED + "4.50";
    private final Index targetIndex;
    private final MoneyOwed amountToLend;

    /**
     * Returns a new LendCommand object that takes in an Index object
     * and a MoneyOwed object.
     *
     * @param targetIndex index of person to lend.
     * @param amountToLend amount to lend to the person.
     */
    public LendCommand(Index targetIndex, MoneyOwed amountToLend) {
        requireNonNull(targetIndex);
        requireNonNull(amountToLend);
        this.targetIndex = targetIndex;
        this.amountToLend = amountToLend;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToLend = lastShownList.get(targetIndex.getZeroBased());
        Person lentPerson;
        try {
            lentPerson = new Person(
                    personToLend.getName(), personToLend.getPhone(), personToLend.getEmail(),
                    personToLend.getAddress(), personToLend.getRemark(), personToLend.getTags(),
                    personToLend.getBirthday(),
                    personToLend.getMoneyOwed().addAmountOwed(amountToLend.getAmount()),
                    personToLend.getDaysAvailable());
        } catch (IllegalArgumentException e) {
            throw new CommandException(MoneyOwed.MESSAGE_CONSTRAINTS);
        }

        model.setPerson(personToLend, lentPerson);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(
                String.format(MESSAGE_LENT_PERSON_SUCCESS, Messages.format(lentPerson)))
                .withPersonToShow(model.findIndex(lentPerson));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LendCommand)) {
            return false;
        }

        LendCommand otherLendCommand = (LendCommand) other;
        return targetIndex.equals(otherLendCommand.targetIndex)
                && amountToLend.equals(otherLendCommand.amountToLend);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("amountToLend", amountToLend)
                .toString();
    }
}
