package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.ExpenseNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.request.ExpenseRequest;
import fr.tobby.tripnjoyback.model.response.BalanceResponse;
import fr.tobby.tripnjoyback.model.response.DebtDetailsResponse;
import fr.tobby.tripnjoyback.model.response.MoneyDueResponse;
import fr.tobby.tripnjoyback.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "expenses")
public class ExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("{group}/purchaser/{user}")
    public ExpenseModel createExpense(@PathVariable("group") long groupId, @PathVariable("user") long userId, @RequestBody ExpenseRequest expenseRequest) {
        return expenseService.createExpense(groupId, userId, expenseRequest);
    }

    @GetMapping("{group}")
    public Collection<ExpenseModel> getExpensesByGroup(@PathVariable("group") long groupId) {
        return expenseService.getExpensesByGroup(groupId);
    }

    @GetMapping("{group}/user/{user}/debts")
    public Collection<MoneyDueResponse> getMoneyUserOwesToEachMemberInGroup(@PathVariable("group") long groupId, @PathVariable("user") long userId) {
        return expenseService.getMoneyUserOwesToEachMemberInGroup(groupId, userId);
    }

    @GetMapping("{group}/user/{user}/debts/due")
    public Collection<MoneyDueResponse> getMoneyEachMemberOwesToUserInGroup(@PathVariable("group") long groupId, @PathVariable("user") long userId) {
        return expenseService.getMoneyEachMemberOwesToUserInGroup(groupId, userId);
    }

    @GetMapping("{group}/user/{user}/debts/details")
    public Collection<DebtDetailsResponse> getUserDebtsDetails(@PathVariable("group") long groupId, @PathVariable("user") long userId) {
        return expenseService.getUserDebtsDetailsInGroup(groupId, userId);
    }

    @GetMapping("{group}/balances")
    public Collection<BalanceResponse> computeBalances(@PathVariable("group") long groupId) {
        return expenseService.computeBalances(groupId);
    }

    @PutMapping("{groupId}/{expenseId}/purchaser/{user}")
    public ExpenseModel updateExpense(@PathVariable("groupId") long groupId, @PathVariable("expenseId") long expenseId, @PathVariable("user") long userId, @RequestBody ExpenseRequest expenseRequest) {
        return expenseService.updateExpense(groupId, expenseId, userId, expenseRequest);
    }

    @DeleteMapping("{groupId}/{expenseId}")
    public void deleteExpense(@PathVariable("groupId") long groupId, @PathVariable("expenseId") long expenseId) {
        expenseService.deleteExpense(groupId, expenseId);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(GroupNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(IllegalArgumentException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UnsupportedOperationException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(ExpenseNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
