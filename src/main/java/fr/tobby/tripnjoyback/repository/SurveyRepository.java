package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface SurveyRepository extends CrudRepository<SurveyEntity,Long> {
    Optional<SurveyEntity> findById(long id);

    Collection<SurveyEntity> findByChannelId(long channelId);
}
