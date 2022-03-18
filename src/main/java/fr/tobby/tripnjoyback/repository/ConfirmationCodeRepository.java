package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCodeEntity, Long> {
    Optional<ConfirmationCodeEntity> findByValue(String value);
}
