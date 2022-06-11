package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends CrudRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByGroupId(long groupId);

    Optional<ExpenseEntity> findById(long expenseId);
}
