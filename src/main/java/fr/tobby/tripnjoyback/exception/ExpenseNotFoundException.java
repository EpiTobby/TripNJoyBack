package fr.tobby.tripnjoyback.exception;

public class ExpenseNotFoundException extends EntityNotFoundException{

    public ExpenseNotFoundException()
    {

    }

    public ExpenseNotFoundException(final String message)
    {
        super(message);
    }
}
