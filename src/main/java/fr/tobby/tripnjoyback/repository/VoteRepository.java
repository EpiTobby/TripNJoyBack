package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoteRepository extends CrudRepository<VoteEntity, Long> {
    Optional<VoteEntity> findByVoterIdAndByAndSurveyId(long voterId, long surveyId);
}
