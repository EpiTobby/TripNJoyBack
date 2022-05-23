package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByPurchaserId(long userId);

    List<ExpenseEntity> findByGroupId(long groupId);
}
