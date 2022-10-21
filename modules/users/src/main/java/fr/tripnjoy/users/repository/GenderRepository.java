package fr.tripnjoy.users.repository;

import fr.tripnjoy.users.entity.GenderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GenderRepository extends CrudRepository<GenderEntity, Long> {

    Optional<GenderEntity> findByValue(String value);
}
