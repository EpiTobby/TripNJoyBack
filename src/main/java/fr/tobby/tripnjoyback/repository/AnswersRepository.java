package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.AnswersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswersRepository extends MongoRepository<AnswersEntity,Long> {
    AnswersEntity findByProfileId(long profileId);

    void deleteByProfileId(Long profileId);
}
