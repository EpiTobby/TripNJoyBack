package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.StateEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StateRepository extends CrudRepository<StateEntity, Long> {
    Optional<StateEntity> findByValue(String value);
}
