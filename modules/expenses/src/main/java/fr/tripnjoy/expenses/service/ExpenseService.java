package fr.tripnjoy.expenses.service;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.expenses.dto.request.ExpenseRequest;
import fr.tripnjoy.expenses.dto.request.MoneyDueRequest;
import fr.tripnjoy.expenses.dto.response.BalanceResponse;
import fr.tripnjoy.expenses.dto.response.DebtDetailsResponse;
import fr.tripnjoy.expenses.dto.response.ExpenseModel;
import fr.tripnjoy.expenses.dto.response.MoneyDueResponse;
import fr.tripnjoy.expenses.entity.ExpenseEntity;
import fr.tripnjoy.expenses.entity.ExpenseMemberEntity;
import fr.tripnjoy.expenses.exception.ExpenseNotFoundException;
import fr.tripnjoy.expenses.repository.ExpenseMemberRepository;
import fr.tripnjoy.expenses.repository.ExpenseRepository;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.groups.dto.response.GroupMemberPublicInfoModel;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseMemberRepository expenseMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final GroupFeignClient groupFeignClient;
    private final UserFeignClient userFeignClient;

    public ExpenseService(ExpenseMemberRepository expenseMemberRepository, ExpenseRepository expenseRepository,
                          final GroupFeignClient groupFeignClient, final UserFeignClient userFeignClient)
    {
        this.expenseMemberRepository = expenseMemberRepository;
        this.expenseRepository = expenseRepository;
        this.groupFeignClient = groupFeignClient;
        this.userFeignClient = userFeignClient;
    }

    @Transactional
    List<ExpenseMemberEntity> addExpenseMembers(ExpenseRequest expenseRequest, ExpenseEntity expenseEntity)
    {
        double amountToPay = expenseRequest.getTotal() / expenseRequest.getMoneyDueByEachUser().size();
        List<ExpenseMemberEntity> expenseMemberEntities = new ArrayList<>();
        expenseRequest.getMoneyDueByEachUser().forEach(moneyDueRequest -> {

            double toPay = expenseRequest.isEvenlyDivided() ? amountToPay : moneyDueRequest.getMoney();
            ExpenseMemberEntity member = new ExpenseMemberEntity(new ExpenseMemberEntity.Ids(expenseEntity, moneyDueRequest.getUserId()), toPay);
            expenseMemberEntities.add(expenseMemberRepository.save(member));
        });
        return expenseMemberEntities;
    }

    @Transactional
    public ExpenseModel createExpense(long groupId, long purchaserId, ExpenseRequest expenseRequest)
    {
        if (!expenseRequest.isEvenlyDivided())
        {
            if (expenseRequest.getMoneyDueByEachUser().stream().anyMatch(r -> r.getMoney() == null) ||
                    Math.round(expenseRequest.getMoneyDueByEachUser().stream().mapToDouble(MoneyDueRequest::getMoney).sum() * 100) / 100 != Math.round(expenseRequest.getTotal() * 100) / 100)
                throw new IllegalArgumentException();
        }
        if (groupFeignClient.isUserInGroup(groupId, purchaserId).value())
            throw new ForbiddenOperationException("User is not in the group");

        ExpenseEntity entity = new ExpenseEntity(-1L, expenseRequest.getTotal(), expenseRequest.getDescription(), purchaserId, groupId, Date.from(Instant.now()), expenseRequest.getIcon());

        entity = expenseRepository.save(entity);
        List<ExpenseMemberEntity> members = addExpenseMembers(expenseRequest, entity);

        return entity.toModel(members);
    }

    public List<MoneyDueResponse> getMoneyUserOwesToEachMemberInGroup(long groupId, long userId)
    {
        if (!userFeignClient.exists(userId).value())
            throw new UserNotFoundException();
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        List<Long> members = groupFeignClient.getInfo(groupId)
                                             .members()
                                             .stream()
                                             .map(GroupMemberPublicInfoModel::id)
                                             .filter(id -> id != userId)
                                             .toList();

        List<MoneyDueResponse> response = new ArrayList<>();
        members.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getIds().getExpense().getPurchaserId() == u && e.getIds().getUserId() == userId).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            sum -= expenseMemberEntities.stream().filter(e -> e.getIds().getExpense().getPurchaserId() == userId && e.getIds().getUserId() == u).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum > 0)
                response.add(new MoneyDueResponse(u, sum));
        });
        return response;
    }

    public List<MoneyDueResponse> getMoneyEachMemberOwesToUserInGroup(long groupId, long userId)
    {
        if (!userFeignClient.exists(userId).value())
            throw new UserNotFoundException();
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        List<MoneyDueResponse> response = new ArrayList<>();
        List<Long> members = groupFeignClient.getInfo(groupId)
                                             .members()
                                             .stream()
                                             .map(GroupMemberPublicInfoModel::id)
                                             .filter(id -> id != userId)
                                             .toList();
        members.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getIds().getExpense().getPurchaserId() == userId && e.getIds().getUserId() == u).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            sum -= expenseMemberEntities.stream().filter(e -> e.getIds().getExpense().getPurchaserId() == u && e.getIds().getUserId() == userId).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum > 0)
                response.add(new MoneyDueResponse(u, sum));
        });
        return response;
    }

    public List<DebtDetailsResponse> getUserDebtsDetailsInGroup(long groupId, long userId)
    {
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return expenseMemberEntities.stream()
                                    .filter(e -> e.getIds().getExpense().getPurchaserId() != userId)
                                    .map(ExpenseMemberEntity::toDebtModel)
                                    .toList();
    }

    public List<BalanceResponse> computeBalances(long groupId)
    {
        Collection<GroupMemberPublicInfoModel> members = groupFeignClient.getInfo(groupId).members();

        List<ExpenseEntity> expenses = expenseRepository.findByGroupId(groupId);

        return members.stream()
                      .map(GroupMemberPublicInfoModel::id)
                      .map(memberId -> {
                          List<ExpenseMemberEntity> debts = expenseMemberRepository.findByGroupIdAndUserId(groupId, memberId);
                          double balance = expenses.stream()
                                                   .filter(e -> e.getPurchaserId() == memberId)
                                                   .mapToDouble(ExpenseEntity::getTotal)
                                                   .sum();
                          balance -= debts.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
                          return new BalanceResponse(memberId, balance);
                      })
                      .toList();
    }

    @Transactional
    public ExpenseModel updateExpense(long groupId, long expenseId, long purchaserId, ExpenseRequest expenseRequest)
    {
        if (!userFeignClient.exists(purchaserId).value())
            throw new UserNotFoundException();
        if (!expenseRequest.isEvenlyDivided())
        {
            if (expenseRequest.getMoneyDueByEachUser().stream().anyMatch(r -> r.getMoney() == null) ||
                    expenseRequest.getMoneyDueByEachUser().stream().mapToDouble(MoneyDueRequest::getMoney).sum() != expenseRequest.getTotal())
                throw new IllegalArgumentException();
        }
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        if (expenseEntity.getGroupId() != groupId)
            throw new IllegalArgumentException();
        expenseMemberRepository.findByExpenseId(expenseEntity.getId()).forEach(expenseMemberRepository::delete);
        expenseEntity.setDescription(expenseRequest.getDescription());
        expenseEntity.setIcon(expenseRequest.getIcon());
        expenseEntity.setTotal(expenseRequest.getTotal());
        expenseEntity.setPurchaserId(purchaserId);
        return expenseEntity.toModel(addExpenseMembers(expenseRequest, expenseEntity));
    }

    @Transactional
    public void deleteExpense(long groupId, long expenseId)
    {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        if (expenseEntity.getGroupId() != groupId)
            throw new IllegalArgumentException();
        expenseRepository.delete(expenseEntity);
    }

    public Collection<ExpenseModel> getExpensesByGroup(long groupId)
    {
        List<ExpenseEntity> expenseEntities = expenseRepository.findByGroupId(groupId);
        return expenseEntities.stream()
                              .map(e -> e.toModel(expenseMemberRepository.findByExpenseId(e.getId())))
                              .toList();
    }
}
