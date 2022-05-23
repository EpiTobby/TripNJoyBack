package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.CreateExpenseRequest;
import fr.tobby.tripnjoyback.model.response.BalanceResponse;
import fr.tobby.tripnjoyback.model.response.DebtResponse;
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
import java.util.stream.Collectors;

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
        double amountToPay = createExpenseRequest.getTotal() / createExpenseRequest.getUserIds().size();
        List<ExpenseMemberEntity> expenseMemberEntities = new ArrayList<ExpenseMemberEntity>();
        createExpenseRequest.getUserIds().stream().forEach(userId -> {
            UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(purchaserId));
            expenseMemberEntities.add(expenseMemberRepository.save(ExpenseMemberEntity.builder()
                    .expense(expenseEntity)
                    .user(user)
                    .amountToPay(amountToPay)
                    .build()));
        });
        return ExpenseModel.of(expenseEntity, expenseMemberEntities);
    }

    public List<DebtResponse> getUserDebtsInGroup(long groupId, long userId){
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByUserId(userId);
        return expenseMemberEntities.stream().filter(e -> e.getExpense().getGroup().getId() == groupId && e.getExpense().getPurchaser().getId() != userId).map(DebtResponse::of).toList();
    }

    public Collection<BalanceResponse> computeBalances(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        List<BalanceResponse> response = new ArrayList<BalanceResponse>() {};
        List<ExpenseEntity> expenses =  expenseRepository.findByGroupId(groupId);
        groupEntity.members.forEach(m -> {
            List<ExpenseMemberEntity> debts = expenseMemberRepository.findByUserId(m.getUser().getId());
            double balance = expenses.stream().filter(e -> e.getPurchaser().getId() == m.getUser().getId()).mapToDouble(ExpenseEntity::getTotal).sum()
                    - debts.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum();
            response.add(new BalanceResponse(UserModel.of(m.getUser()), balance));
        });
        return response;
    }
}
