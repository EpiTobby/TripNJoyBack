package fr.tripnjoy.expenses.api.client;

import fr.tripnjoy.expenses.dto.request.ExpenseRequest;
import fr.tripnjoy.expenses.dto.response.BalanceResponse;
import fr.tripnjoy.expenses.dto.response.DebtDetailsResponse;
import fr.tripnjoy.expenses.dto.response.ExpenseModel;
import fr.tripnjoy.expenses.dto.response.MoneyDueResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@FeignClient(value = "SERVICE-EXPENSES")
public interface ExpenseFeignClient {

    @PostMapping("{group}/purchaser/{user}")
    ExpenseModel createExpense(@PathVariable("group") long groupId, @PathVariable("user") long userId, @RequestBody ExpenseRequest expenseRequest);

    @GetMapping("{group}")
    Collection<ExpenseModel> getExpensesByGroup(@PathVariable("group") long groupId);

    @GetMapping("{group}/user/{user}/debts")
    Collection<MoneyDueResponse> getMoneyUserOwesToEachMemberInGroup(@PathVariable("group") long groupId, @PathVariable("user") long userId);

    @GetMapping("{group}/user/{user}/debts/due")
    Collection<MoneyDueResponse> getMoneyEachMemberOwesToUserInGroup(@PathVariable("group") long groupId, @PathVariable("user") long userId);

    @GetMapping("{group}/user/{user}/debts/details")
    Collection<DebtDetailsResponse> getUserDebtsDetails(@PathVariable("group") long groupId, @PathVariable("user") long userId);

    @GetMapping("{group}/balances")
    Collection<BalanceResponse> computeBalances(@PathVariable("group") long groupId);

    @PutMapping("{groupId}/{expenseId}/purchaser/{user}")
    ExpenseModel updateExpense(@PathVariable("groupId") long groupId, @PathVariable("expenseId") long expenseId, @PathVariable("user") long userId, @RequestBody ExpenseRequest expenseRequest);

    @DeleteMapping("{groupId}/{expenseId}")
    void deleteExpense(@PathVariable("groupId") long groupId, @PathVariable("expenseId") long expenseId);
}
