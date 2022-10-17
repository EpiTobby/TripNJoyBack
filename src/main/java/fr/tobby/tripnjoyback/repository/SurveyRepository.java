package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface SurveyRepository extends CrudRepository<SurveyEntity,Long> {
    Collection<SurveyEntity> findByChannelId(long channelId);
}
