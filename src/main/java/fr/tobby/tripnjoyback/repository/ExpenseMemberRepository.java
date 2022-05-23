package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<ExpenseEntity, Long> {
    void findByPurchaserId(long userId);
}
