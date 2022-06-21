package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseMemberRepository extends CrudRepository<ExpenseMemberEntity, Long> {
    List<ExpenseMemberEntity> findByExpenseId(long expenseId);

    @Query("SELECT e from ExpenseMemberEntity e WHERE e.expense.group.id = :groupId AND e.user.id = :userId")
    List<ExpenseMemberEntity> findByGroupIdAndUserId(@Param("groupId") long groupId, @Param("userId") long userId);

    @Query("SELECT e from ExpenseMemberEntity e WHERE e.expense.group.id = :groupId")
    List<ExpenseMemberEntity> findByGroupId(@Param("groupId") long groupId);
}
