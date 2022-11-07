package fr.tripnjoy.expenses.repository;


import fr.tripnjoy.expenses.entity.ExpenseMemberEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseMemberRepository extends CrudRepository<ExpenseMemberEntity, Long> {
    @Query("SELECT e FROM ExpenseMemberEntity e WHERE e.ids.expense.id = :expenseId")
    List<ExpenseMemberEntity> findByExpenseId(long expenseId);

    @Query("SELECT e from ExpenseMemberEntity e WHERE e.ids.expense.groupId = :groupId AND e.ids.userId = :userId")
    List<ExpenseMemberEntity> findByGroupIdAndUserId(@Param("groupId") long groupId, @Param("userId") long userId);

    @Query("SELECT e from ExpenseMemberEntity e WHERE e.ids.expense.groupId = :groupId")
    List<ExpenseMemberEntity> findByGroupId(@Param("groupId") long groupId);
}
