package fr.tripnjoy.planning.repository;

import fr.tripnjoy.planning.entity.ActivityMemberEntity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityMemberRepository extends CrudRepository<ActivityMemberEntity, ActivityMemberEntity.Ids> {
}
