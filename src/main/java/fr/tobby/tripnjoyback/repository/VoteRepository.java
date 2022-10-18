package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends CrudRepository<VoteEntity, Long> {
    Optional<VoteEntity> findByVoterIdAndSurveyId(long voterId, long surveyId);

    List<VoteEntity> findBySurveyId(long surveyId);
}
