package fr.tripnjoy.profiles.repository;

import fr.tripnjoy.profiles.entity.UserProfileEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserProfileRepository extends CrudRepository<UserProfileEntity, UserProfileEntity.Ids> {
}
