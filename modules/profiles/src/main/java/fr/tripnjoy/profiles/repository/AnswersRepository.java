package fr.tripnjoy.profiles.repository;

import fr.tripnjoy.profiles.entity.AnswersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswersRepository extends MongoRepository<AnswersEntity,String> {
    AnswersEntity findByProfileId(long profileId);

    void deleteByProfileId(Long profileId);
}
