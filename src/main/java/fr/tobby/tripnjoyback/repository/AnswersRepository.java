package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.AnswersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswersRepository extends MongoRepository<AnswersEntity,String> {
    AnswersEntity findByProfileId(long profileId);

    void deleteByProfileId(Long profileId);
}
