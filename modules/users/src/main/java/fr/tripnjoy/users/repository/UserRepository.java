package fr.tripnjoy.users.repository;

import fr.tripnjoy.users.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    UserEntity getById(long userId);

    @Override
    Collection<UserEntity> findAll();
}
