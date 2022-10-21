package fr.tripnjoy.users.repository;

import fr.tripnjoy.users.entity.LanguageEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LanguageRepository  extends CrudRepository<LanguageEntity, Long> {

    Optional<LanguageEntity> findByValue(String value);
}
