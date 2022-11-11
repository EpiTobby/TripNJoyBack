package fr.tripnjoy.reports.repository;


import fr.tripnjoy.reports.entity.ReportEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends CrudRepository<ReportEntity, Long> {
    Optional<ReportEntity> findById(long reportId);

    List<ReportEntity> findBySubmitter(long submitterId);

    List<ReportEntity> findByReportedUser(long reportedUserId);
}
