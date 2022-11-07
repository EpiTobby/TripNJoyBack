package fr.tripnjoy.expenses.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class ExpenseNotFoundException extends EntityNotFoundException {

    public ExpenseNotFoundException()
    {

    }

    public ExpenseNotFoundException(final String message)
    {
        super(message);
    }
}
