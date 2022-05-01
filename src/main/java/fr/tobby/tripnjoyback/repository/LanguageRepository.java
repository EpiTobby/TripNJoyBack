package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.LanguageEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LanguageRepository  extends CrudRepository<LanguageEntity, Long> {

    Optional<LanguageEntity> findByValue(String value);
}
