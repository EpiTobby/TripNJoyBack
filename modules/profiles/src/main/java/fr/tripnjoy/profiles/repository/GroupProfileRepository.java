package fr.tripnjoy.profiles.repository;

import fr.tripnjoy.profiles.entity.GroupProfileEntity;
import org.springframework.data.repository.CrudRepository;

public interface GroupProfileRepository extends CrudRepository<GroupProfileEntity, GroupProfileEntity.Ids> {
}
