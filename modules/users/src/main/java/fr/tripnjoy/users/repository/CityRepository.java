package fr.tripnjoy.users.repository;

import fr.tripnjoy.users.entity.CityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CityRepository extends CrudRepository<CityEntity, Long> {

    Optional<CityEntity> findByName(String name);
}
