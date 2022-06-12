package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ReportEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends CrudRepository<ReportEntity, Long> {
    Optional<ReportEntity> findById(long reportId);

    List<ReportEntity> findBySubmitter(long submitterId);
}
