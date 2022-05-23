package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ExpenseMemberModel;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.request.CreateExpenseRequest;
import fr.tobby.tripnjoyback.repository.ExpenseMemberRepository;
import fr.tobby.tripnjoyback.repository.ExpenseRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;

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
        GroupEntity groupEntity  = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(purchaserId));
        ExpenseEntity expenseEntity = expenseRepository.save(
                ExpenseEntity.builder()
                        .purchaser(purchaser)
                        .total(createExpenseRequest.getTotal())
                        .description(createExpenseRequest.getDescription())
                        .groupEntity(groupEntity)
                        .date(Date.from(Instant.now()))
                        .build()
        );
        Double amountToPay = createExpenseRequest.getTotal() / (createExpenseRequest.getUserIds().size() + 1);
        List<ExpenseMemberEntity> expenseMemberEntites = List.of();
        createExpenseRequest.getUserIds().stream().forEach(userId -> {
            UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(purchaserId));
            expenseMemberEntites.add(expenseMemberRepository.save(ExpenseMemberEntity.builder()
                    .expense(expenseEntity)
                    .user(user)
                    .amountToPay(amountToPay)
                    .build()));
        });
        return ExpenseModel.of(expenseEntity, expenseMemberEntites);
    }
}
