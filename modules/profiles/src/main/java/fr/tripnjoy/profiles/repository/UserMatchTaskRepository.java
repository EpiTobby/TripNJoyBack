package fr.tripnjoy.profiles.repository;

import fr.tripnjoy.profiles.entity.UserMatchTaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserMatchTaskRepository extends CrudRepository<UserMatchTaskEntity, Long> {

    @Query("SELECT u.userId FROM UserMatchTaskEntity u")
    List<Long> findAllWaitingUserIds();
}
