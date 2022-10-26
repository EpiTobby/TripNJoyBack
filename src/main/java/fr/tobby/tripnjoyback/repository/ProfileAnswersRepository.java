package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ProfileAnswersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileAnswersRepository extends MongoRepository<ProfileAnswersEntity,String> {
    ProfileAnswersEntity findByProfileId(long profileId);

    void deleteByProfileId(Long profileId);
}
