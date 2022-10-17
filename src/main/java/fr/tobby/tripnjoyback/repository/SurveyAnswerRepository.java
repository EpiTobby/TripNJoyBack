package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SurveyAnswerRepository extends CrudRepository<SurveyAnswerEntity, Long> {
    Optional<SurveyAnswerEntity> findById(long id);
}
