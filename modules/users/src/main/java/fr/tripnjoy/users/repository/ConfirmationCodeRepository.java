package fr.tripnjoy.users.repository;

import fr.tripnjoy.users.entity.ConfirmationCodeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCodeEntity, Long> {
    Optional<ConfirmationCodeEntity> findByValue(String value);

    Optional<ConfirmationCodeEntity> findByUserId(long id);
}
