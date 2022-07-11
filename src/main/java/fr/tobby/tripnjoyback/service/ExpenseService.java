package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.ExpenseNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.request.ExpenseRequest;
import fr.tobby.tripnjoyback.model.request.MoneyDueRequest;
import fr.tobby.tripnjoyback.model.response.BalanceResponse;
import fr.tobby.tripnjoyback.model.response.DebtDetailsResponse;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
import fr.tobby.tripnjoyback.model.response.MoneyDueResponse;
import fr.tobby.tripnjoyback.repository.ExpenseMemberRepository;
import fr.tobby.tripnjoyback.repository.ExpenseRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ExpenseService {
    private final ExpenseMemberRepository expenseMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseMemberRepository expenseMemberRepository, ExpenseRepository expenseRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.expenseMemberRepository = expenseMemberRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    List<ExpenseMemberEntity> addExpenseMembers(ExpenseRequest expenseRequest, ExpenseEntity expenseEntity) {
        double amountToPay = expenseRequest.getTotal() / expenseRequest.getMoneyDueByEachUser().size();
        List<ExpenseMemberEntity> expenseMemberEntities = new ArrayList<>();
        expenseRequest.getMoneyDueByEachUser().forEach(moneyDueRequest -> {
            UserEntity user = userRepository.findById(moneyDueRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(moneyDueRequest.getUserId()));
            expenseMemberEntities.add(expenseMemberRepository.save(ExpenseMemberEntity.builder()
                    .expense(expenseEntity)
                    .user(user)
                    .amountToPay(expenseRequest.isEvenlyDivided() ? amountToPay : moneyDueRequest.getMoney())
                    .build()));
        });
        return expenseMemberEntities;
    }

    @Transactional
    public ExpenseModel createExpense(long groupId, long purchaserId, ExpenseRequest expenseRequest) {
        if (!expenseRequest.isEvenlyDivided()) {
            if (expenseRequest.getMoneyDueByEachUser().stream().anyMatch(r -> r.getMoney() == null) ||
                    Math.round(expenseRequest.getMoneyDueByEachUser().stream().mapToDouble(MoneyDueRequest::getMoney).sum() * 100) / 100 != Math.round(expenseRequest.getTotal() * 100) / 100)
                throw new IllegalArgumentException();
        }
        UserEntity purchaser = userRepository.findById(purchaserId).orElseThrow(() -> new UserNotFoundException(purchaserId));
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        ExpenseEntity expenseEntity = expenseRepository.save(
                ExpenseEntity.builder()
                        .purchaser(purchaser)
                        .total(expenseRequest.getTotal())
                        .description(expenseRequest.getDescription())
                        .group(groupEntity)
                        .date(Date.from(Instant.now()))
                        .icon(expenseRequest.getIcon())
                        .build()
        );

        return ExpenseModel.of(expenseEntity, addExpenseMembers(expenseRequest, expenseEntity));
    }

    public List<MoneyDueResponse> getMoneyUserOwesToEachMemberInGroup(long groupId, long userId) {
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        Stream<UserEntity> userEntities = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId)).members.stream().map(GroupMemberEntity::getUser).filter(u -> u.getId() != userId);
        List<MoneyDueResponse> response = new ArrayList<>() {
        };
        userEntities.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId().equals(u.getId()) && e.getUser().getId() == userId).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            sum -= expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId() == userId && e.getUser().getId().equals(u.getId())).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum > 0)
                response.add(new MoneyDueResponse(GroupMemberModel.of(u), sum));
        });
        return response;
    }

    public List<MoneyDueResponse> getMoneyEachMemberOwesToUserInGroup(long groupId, long userId) {
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        List<MoneyDueResponse> response = new ArrayList<>() {
        };
        Stream<UserEntity> userEntities = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId)).members.stream().map(GroupMemberEntity::getUser).filter(u -> u.getId() != userId);
        userEntities.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId() == userId && e.getUser().getId().equals(u.getId())).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            sum -= expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId().equals(u.getId()) && e.getUser().getId() == userId).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum > 0)
                response.add(new MoneyDueResponse(GroupMemberModel.of(u), sum));
        });
        return response;
    }

    public List<DebtDetailsResponse> getUserDebtsDetailsInGroup(long groupId, long userId) {
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId() != userId).map(DebtDetailsResponse::of).toList();
    }

    public List<BalanceResponse> computeBalances(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        List<BalanceResponse> response = new ArrayList<>() {
        };
        List<ExpenseEntity> expenses = expenseRepository.findByGroupId(groupId);
        groupEntity.members.forEach(m -> {
            List<ExpenseMemberEntity> debts = expenseMemberRepository.findByGroupIdAndUserId(groupId, m.getUser().getId());
            double balance = expenses.stream().filter(e -> e.getPurchaser().getId().equals(m.getUser().getId())).mapToDouble(ExpenseEntity::getTotal).sum()
                    - debts.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            response.add(new BalanceResponse(GroupMemberModel.of(m.getUser()), balance));
        });
        return response;
    }

    @Transactional
    public ExpenseModel updateExpense(long groupId, long expenseId, long purchaserId, ExpenseRequest expenseRequest) {
        if (!expenseRequest.isEvenlyDivided()) {
            if (expenseRequest.getMoneyDueByEachUser().stream().anyMatch(r -> r.getMoney() == null) ||
                    expenseRequest.getMoneyDueByEachUser().stream().mapToDouble(MoneyDueRequest::getMoney).sum() != expenseRequest.getTotal())
                throw new IllegalArgumentException();
        }
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        if (expenseEntity.getGroup().getId() != groupId)
            throw new ForbiddenOperationException("You cannot perform this operation");
        expenseMemberRepository.findByExpenseId(expenseEntity.getId()).forEach(expenseMemberRepository::delete);
        expenseEntity.setDescription(expenseRequest.getDescription());
        expenseEntity.setIcon(expenseRequest.getIcon());
        expenseEntity.setTotal(expenseRequest.getTotal());
        expenseEntity.setPurchaser(userRepository.findById(purchaserId).orElseThrow(() -> new UserNotFoundException(purchaserId)));
        return ExpenseModel.of(expenseEntity, addExpenseMembers(expenseRequest, expenseEntity));
    }

    @Transactional
    public void deleteExpense(long groupId, long expenseId) {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        if (expenseEntity.getGroup().getId() != groupId)
            throw new ForbiddenOperationException("You cannot perform this operation");
        expenseRepository.delete(expenseEntity);
    }

    public Collection<ExpenseModel> getExpensesByGroup(long groupId) {
        List<ExpenseEntity> expenseEntities = expenseRepository.findByGroupId(groupId);
        return expenseEntities.stream().map(e -> ExpenseModel.of(e, expenseMemberRepository.findByExpenseId(e.getId()))).toList();
    }
}
