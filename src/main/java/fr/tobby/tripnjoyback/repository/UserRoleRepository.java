package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    RoleEntity getByName(String name);
}
