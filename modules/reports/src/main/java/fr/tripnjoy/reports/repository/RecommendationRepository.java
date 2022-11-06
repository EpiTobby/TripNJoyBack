package fr.tripnjoy.reports.repository;


import fr.tripnjoy.reports.entity.RecommendationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends CrudRepository<RecommendationEntity, Long> {
    Optional<RecommendationEntity> findById(long reportId);

    List<RecommendationEntity> findByRecommendedUserId(long reportedUserId);
}
