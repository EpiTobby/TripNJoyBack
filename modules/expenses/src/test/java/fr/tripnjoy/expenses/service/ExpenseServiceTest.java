package fr.tripnjoy.expenses.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.expenses.dto.request.ExpenseRequest;
import fr.tripnjoy.expenses.dto.request.MoneyDueRequest;
import fr.tripnjoy.expenses.dto.response.BalanceResponse;
import fr.tripnjoy.expenses.dto.response.ExpenseModel;
import fr.tripnjoy.expenses.dto.response.MoneyDueResponse;
import fr.tripnjoy.expenses.entity.ExpenseMemberEntity;
import fr.tripnjoy.expenses.repository.ExpenseMemberRepository;
import fr.tripnjoy.expenses.repository.ExpenseRepository;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.users.api.client.UserFeignClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.mockito.Mockito.*;

@DataJpaTest
class ExpenseServiceTest {
    @Autowired
    private ExpenseMemberRepository expenseMemberRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    private ExpenseService expenseService;

    private GroupFeignClient groupFeignClient;
    private UserFeignClient userFeignClient;

    long groupIdCounter = 1;
    long userIdCounter = 1;

    private long anyGroup()
    {
        return groupIdCounter++;
    }

    private long anyUserInGroup(long groupId)
    {
        when(groupFeignClient.isUserInGroup(groupId, userIdCounter)).thenReturn(new BooleanResponse(true));
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    @BeforeEach
    void setUp()
    {
        groupFeignClient = mock(GroupFeignClient.class);
        userFeignClient = mock(UserFeignClient.class);
        expenseService = new ExpenseService(expenseMemberRepository, expenseRepository, groupFeignClient, userFeignClient);
    }

    @AfterEach
    void tearDown()
    {
        expenseRepository.deleteAll();
        expenseMemberRepository.deleteAll();
        reset(groupFeignClient);
        reset(userFeignClient);
    }

    @Test
    void createExpenseEvenlyDivided()
    {
        long groupId = anyGroup();
        long user1Id = anyUserInGroup(groupId);
        long user2Id = anyUserInGroup(groupId);
        long user3Id = anyUserInGroup(groupId);

        ExpenseRequest request = new ExpenseRequest("Food",
                60,
                List.of(new MoneyDueRequest(user1Id, null), new MoneyDueRequest(user2Id, null), new MoneyDueRequest(user3Id, null)),
                true,
                null);

        ExpenseModel expenseModel = expenseService.createExpense(groupId, user1Id, request);

        Assertions.assertFalse(expenseRepository.findById(expenseModel.getId()).isEmpty());

        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        Assertions.assertEquals(3, expenseMemberEntities.size());
        Assertions.assertEquals(20, expenseMemberEntities.get(0).getAmountToPay());
        Assertions.assertEquals(60, expenseMemberEntities.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum());
    }

    @Test
    void createExpenseNotEvenlyDivided()
    {
        long groupId = anyGroup();
        long user1 = anyUserInGroup(groupId);
        long user2 = anyUserInGroup(groupId);
        long user3 = anyUserInGroup(groupId);

        ExpenseRequest request = new ExpenseRequest("Food",
                60,
                List.of(new MoneyDueRequest(user1, 10d), new MoneyDueRequest(user2, 20d), new MoneyDueRequest(user3, 30d)),
                false,
                null);

        ExpenseModel expenseModel = expenseService.createExpense(groupId, user1, request);

        Assertions.assertFalse(expenseRepository.findById(expenseModel.getId()).isEmpty());

        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        Assertions.assertEquals(3, expenseMemberEntities.size());
        Assertions.assertEquals(60, expenseMemberEntities.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum());
    }

    @Test
    void MoneyUserOwes()
    {
        long groupId = anyGroup();
        long user1 = anyUserInGroup(groupId);
        long user2 = anyUserInGroup(groupId);
        long user3 = anyUserInGroup(groupId);
        when(groupFeignClient.getMembers(groupId)).thenReturn(List.of(user1, user2, user3));

        ExpenseRequest request1 = new ExpenseRequest("Food",
                60,
                List.of(new MoneyDueRequest(user1, 10d), new MoneyDueRequest(user2, 20d), new MoneyDueRequest(user3, 30d)),
                false,
                null);

        ExpenseRequest request2 = new ExpenseRequest("Food",
                30,
                List.of(new MoneyDueRequest(user1, 6d), new MoneyDueRequest(user2, 12d), new MoneyDueRequest(user3, 12d)),
                false,
                null);

        expenseService.createExpense(groupId, user3, request1);
        expenseService.createExpense(groupId, user3, request2);
        List<MoneyDueResponse> moneyDueResponses = expenseService.getMoneyUserOwesToEachMemberInGroup(groupId, user1);

        Assertions.assertEquals(16, moneyDueResponses.get(0).getTotal());
        moneyDueResponses = expenseService.getMoneyUserOwesToEachMemberInGroup(groupId, user2);
        Assertions.assertEquals(32, moneyDueResponses.get(0).getTotal());
    }

    @Test
    void MembersOwesToUser()
    {
        long groupId = anyGroup();
        long user1 = anyUserInGroup(groupId);
        long user2 = anyUserInGroup(groupId);
        long user3 = anyUserInGroup(groupId);
        when(groupFeignClient.getMembers(groupId)).thenReturn(List.of(user1, user2, user3));

        ExpenseRequest request1 = new ExpenseRequest("Food",
                60,
                List.of(new MoneyDueRequest(user1, 10d), new MoneyDueRequest(user2, 20d), new MoneyDueRequest(user3, 30d)),
                false,
                null);

        ExpenseRequest request2 = new ExpenseRequest("Food",
                30,
                List.of(new MoneyDueRequest(user1, 6d), new MoneyDueRequest(user2, 12d), new MoneyDueRequest(user3, 12d)),
                false,
                null);

        expenseService.createExpense(groupId, user3, request1);
        expenseService.createExpense(groupId, user3, request2);

        List<MoneyDueResponse> moneyDueResponses = expenseService.getMoneyEachMemberOwesToUserInGroup(groupId, user3);
        Assertions.assertEquals(16, moneyDueResponses.get(0).getTotal());
        Assertions.assertEquals(32, moneyDueResponses.get(1).getTotal());
    }

    @Test
    void balance()
    {
        long groupId = anyGroup();
        long user1 = anyUserInGroup(groupId);
        long user2 = anyUserInGroup(groupId);
        long user3 = anyUserInGroup(groupId);
        when(groupFeignClient.getMembers(groupId)).thenReturn(List.of(user1, user2, user3));

        ExpenseRequest request1 = new ExpenseRequest("Food",
                60,
                List.of(new MoneyDueRequest(user1, 10d), new MoneyDueRequest(user2, 20d), new MoneyDueRequest(user3, 30d)),
                false,
                null);

        ExpenseRequest request2 = new ExpenseRequest("Food",
                30,
                List.of(new MoneyDueRequest(user1, 6d), new MoneyDueRequest(user2, 12d), new MoneyDueRequest(user3, 12d)),
                false,
                null);

        expenseService.createExpense(groupId, user3, request1);
        expenseService.createExpense(groupId, user3, request2);

        List<BalanceResponse> balanceResponses = expenseService.computeBalances(groupId);

        Assertions.assertEquals(48, balanceResponses.get(2).getMoney());
        Assertions.assertEquals(0, balanceResponses.stream().mapToDouble(BalanceResponse::getMoney).sum());
    }
}
