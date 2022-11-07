package fr.tripnjoy.chat.repository;

import fr.tripnjoy.chat.entity.SurveyAnswerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyAnswerRepository extends CrudRepository<SurveyAnswerEntity, Long> {
    Optional<SurveyAnswerEntity> findById(long id);

    List<SurveyAnswerEntity> findBySurveyId(long surveyId);
}
