package fr.tripnjoy.chat.repository;

import fr.tripnjoy.chat.entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends CrudRepository<VoteEntity, Long> {

    Optional<VoteEntity> findByVoterAndSurveyIdAndAnswerId(long voterId, long surveyId, long answerId);

    Optional<VoteEntity> findByVoterAndSurveyId(long voterId, long surveyId);

    List<VoteEntity> findBySurveyId(long surveyId);
}
