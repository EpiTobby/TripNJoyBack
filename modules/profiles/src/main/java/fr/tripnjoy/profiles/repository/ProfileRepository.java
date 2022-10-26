package fr.tripnjoy.profiles.repository;

import fr.tripnjoy.profiles.entity.ProfileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findById(long profileId);

    @Query("SELECT u.ids.profile FROM UserProfileEntity u WHERE u.ids.userId = ?1")
    List<ProfileEntity> findByUserId(long userId);

    List<ProfileEntity> findByActiveIsTrue();

    ProfileEntity getById(long id);
}
