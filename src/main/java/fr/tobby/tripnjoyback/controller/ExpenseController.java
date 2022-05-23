package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.request.CreateExpenseRequest;
import fr.tobby.tripnjoyback.model.response.BalanceResponse;
import fr.tobby.tripnjoyback.model.response.DebtResponse;
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
    public ExpenseModel createExpense(@PathVariable("group") long groupId, @PathVariable("user") long userId, @RequestBody CreateExpenseRequest createExpenseRequest){
        return expenseService.createExpense(groupId, userId, createExpenseRequest);
    }

    @GetMapping("{group}/user/{user}/debts")
    public Collection<DebtResponse> getUserDebts(@PathVariable("group") long groupId, @PathVariable("user") long userId){
        return expenseService.getUserDebtsInGroup(groupId, userId);
    }

    @GetMapping("{group}/balances")
    public Collection<BalanceResponse> computeBalances(@PathVariable("group") long groupId){
        return expenseService.computeBalances(groupId);
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
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(GroupNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
