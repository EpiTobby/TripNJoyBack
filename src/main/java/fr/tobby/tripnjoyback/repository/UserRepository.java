package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
