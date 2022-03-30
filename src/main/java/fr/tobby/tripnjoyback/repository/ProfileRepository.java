package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ProfileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Long> {
    List<ProfileEntity> findByUserId(long userId);
}
