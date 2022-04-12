package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Collection<UserEntity> findAllByWaitingForGroupIsTrue();
}
