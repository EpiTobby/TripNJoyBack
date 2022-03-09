package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.CityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CityRepository extends CrudRepository<CityEntity, Long> {

    Optional<CityEntity> findByName(String name);
}
