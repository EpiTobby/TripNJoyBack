package fr.tripnjoy.reports.service;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.profiles.exception.ReportNotFoundException;
import fr.tripnjoy.profiles.model.ReportReason;
import fr.tripnjoy.reports.entity.ReportEntity;
import fr.tripnjoy.reports.model.ReportModel;
import fr.tripnjoy.reports.repository.ReportRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserFeignClient userFeignClient;

    public ReportService(ReportRepository reportRepository, final UserFeignClient userFeignClient)
    {
        this.reportRepository = reportRepository;
        this.userFeignClient = userFeignClient;
    }

    @Transactional
    public ReportModel submitReport(long submitterId, long userIdToReport, ReportReason reason, String details)
    {
        if (!userFeignClient.exists(userIdToReport).value())
            throw new UserNotFoundException("No user found with id:" + userIdToReport);

        if (userIdToReport == submitterId)
            throw new ForbiddenOperationException("You cannot report yourself");

        ReportEntity reportEntity = reportRepository.save(new ReportEntity(submitterId, userIdToReport, reason.toString(), details, Instant.now()));
        // FIXME: prom
//        promStats.getReportCount().set(reportRepository.count());
        return ReportModel.of(reportEntity);
    }

    public List<ReportModel> getBySubmitterId(long submitterId)
    {
        return reportRepository.findBySubmitter(submitterId).stream().map(ReportModel::of).toList();
    }

    public List<ReportModel> getByReportedUserId(long userId)
    {
        return reportRepository.findByReportedUser(userId).stream().map(ReportModel::of).toList();
    }

    public int getReportCountForUser(long userId)
    {
        return getByReportedUserId(userId).size();
    }

    @Transactional
    public ReportModel updateReport(long userId, long reportId, ReportReason reason, String details)
    {
        ReportEntity reportEntity = getReportEntity(reportId);
        if (reportEntity.getSubmitter() != userId)
            throw new ForbiddenOperationException("You cannot perform this operation.");
        reportEntity.setReason(reason.toString());
        reportEntity.setDetails(details);
        return ReportModel.of(reportEntity);
    }

    private ReportEntity getReportEntity(final long reportId) throws ReportNotFoundException
    {
        return reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("No report found with id: " + reportId));
    }

    @Transactional
    public void deleteReport(long userId, long reportId)
    {
        ReportEntity reportEntity = getReportEntity(reportId);
        if (reportEntity.getSubmitter() != userId)
            throw new ForbiddenOperationException("You cannot perform this operation.");
        deleteReport(reportEntity);
    }

    @Transactional
    public void deleteReportAdmin(long reportId)
    {
        ReportEntity reportEntity = getReportEntity(reportId);
        deleteReport(reportEntity);
    }

    @Transactional
    protected void deleteReport(ReportEntity reportEntity)
    {
        reportRepository.delete(reportEntity);
        // FIXME: prom
//        promStats.getReportCount().set(reportRepository.count());
    }
}
