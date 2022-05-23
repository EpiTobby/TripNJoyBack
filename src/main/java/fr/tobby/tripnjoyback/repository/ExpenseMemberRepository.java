package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseMemberRepository extends CrudRepository<ExpenseMemberEntity, Long> {
    List<ExpenseMemberEntity> findByExpenseId(long expenseId);

    List<ExpenseMemberEntity> findByUserId(long userId);
}
