package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.CreateExpenseRequest;
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
    public ExpenseModel createExpense(long groupId, long purchaserId, CreateExpenseRequest createExpenseRequest){
        UserEntity purchaser = userRepository.findById(purchaserId).orElseThrow(() -> new UserNotFoundException(purchaserId));
        GroupEntity groupEntity  = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        ExpenseEntity expenseEntity = expenseRepository.save(
                ExpenseEntity.builder()
                        .purchaser(purchaser)
                        .total(createExpenseRequest.getTotal())
                        .description(createExpenseRequest.getDescription())
                        .group(groupEntity)
                        .date(Date.from(Instant.now()))
                        .build()
        );
        double amountToPay = createExpenseRequest.getTotal() / createExpenseRequest.getMoneyDueByEachUser().size();
        List<ExpenseMemberEntity> expenseMemberEntities = new ArrayList<ExpenseMemberEntity>();
        createExpenseRequest.getMoneyDueByEachUser().stream().forEach(moneyDueRequest -> {
            UserEntity user = userRepository.findById(moneyDueRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(purchaserId));
            expenseMemberEntities.add(expenseMemberRepository.save(ExpenseMemberEntity.builder()
                    .expense(expenseEntity)
                    .user(user)
                    .amountToPay(createExpenseRequest.isEvenlyDivided() ? amountToPay : moneyDueRequest.getMoney())
                    .build()));
        });
        return ExpenseModel.of(expenseEntity, expenseMemberEntities);
    }

    public List<MoneyDueResponse> getMoneyUserOwesToEachMemberInGroup(long groupId, long userId){
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        Stream<UserEntity> userEntities = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId)).members.stream().map(GroupMemberEntity::getUser).filter(u -> u.getId() != userId);
        List<MoneyDueResponse> response = new ArrayList<MoneyDueResponse>() {};
        userEntities.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId().equals(u.getId()) && e.getUser().getId() == userId).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum != 0)
                response.add(new MoneyDueResponse(GroupMemberModel.of(u), sum));
        });
        return response;
    }

    public List<MoneyDueResponse> getMoneyEachMemberOwesToUserInGroup(long groupId, long userId){
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupId);
        List<MoneyDueResponse> response = new ArrayList<MoneyDueResponse>() {};
        Stream<UserEntity> userEntities = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId)).members.stream().map(GroupMemberEntity::getUser).filter(u -> u.getId() != userId);
        userEntities.forEach(u -> {
            double sum = expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId() == userId && e.getUser().getId().equals(u.getId())).mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            if (sum != 0)
                response.add(new MoneyDueResponse(GroupMemberModel.of(u), sum));
        });
        return response;
    }

    public List<DebtDetailsResponse> getUserDebtsDetailsInGroup(long groupId, long userId){
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return expenseMemberEntities.stream().filter(e -> e.getExpense().getPurchaser().getId() != userId).map(DebtDetailsResponse::of).toList();
    }

    public Collection<BalanceResponse> computeBalances(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        List<BalanceResponse> response = new ArrayList<BalanceResponse>() {};
        List<ExpenseEntity> expenses =  expenseRepository.findByGroupId(groupId);
        groupEntity.members.forEach(m -> {
            List<ExpenseMemberEntity> debts = expenseMemberRepository.findByGroupIdAndUserId(groupId, m.getUser().getId());
            double balance = expenses.stream().filter(e -> e.getPurchaser().getId().equals(m.getUser().getId())).mapToDouble(ExpenseEntity::getTotal).sum()
                    - debts.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            response.add(new BalanceResponse(GroupMemberModel.of(m.getUser()), balance));
        });
        return response;
    }
}
