package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ProfileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findById(long profileId);

    @Query("SELECT p FROM ProfileEntity p WHERE p.id = (SELECT u.id FROM UserEntity u WHERE u.id = ?1)")
    List<ProfileEntity> findByUserId(long userId);

    List<ProfileEntity> findByActiveIsTrue();

    @Query("SELECT p FROM ProfileEntity p WHERE p.active = true AND p.id = (SELECT u.id FROM UserEntity u WHERE u.id = ?1)")
    Optional<ProfileEntity> findByActiveIsTrueAndUserId(long userId);

    ProfileEntity getById(long id);
}
