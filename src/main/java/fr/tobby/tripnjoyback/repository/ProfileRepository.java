package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ProfileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findById(long profileId);

    @Query("SELECT u.profiles FROM UserEntity u WHERE u.id = ?1")
    List<ProfileEntity> findByUserId(long userId);

    List<ProfileEntity> findByActiveIsTrue();

    ProfileEntity getById(long id);
}
