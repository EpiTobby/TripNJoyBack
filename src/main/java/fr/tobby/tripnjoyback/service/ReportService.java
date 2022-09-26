package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ReportEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.ReportNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.ReportModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.SubmitReportRequest;
import fr.tobby.tripnjoyback.model.request.UpdateReportRequest;
import fr.tobby.tripnjoyback.repository.ReportRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final IdCheckerService idCheckerService;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository, IdCheckerService idCheckerService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.idCheckerService = idCheckerService;
    }

    public List<ReportModel> getAll(){
        List<ReportModel> reports = new ArrayList<>();
        reportRepository.findAll().forEach(reportEntity -> reports.add(ReportModel.of(reportEntity)));
        return reports;
    }

    @Transactional
    public ReportModel submitReport(String submitterEmail, SubmitReportRequest submitReportRequest){
        UserEntity submitter = userRepository.findByEmail(submitterEmail).orElseThrow(() -> new UserNotFoundException("No user found with email:" + submitterEmail));
        UserEntity reportedUser =  userRepository.findById(submitReportRequest.getReportedUserId()).orElseThrow(() -> new UserNotFoundException("No user found with id:" + submitReportRequest.getReportedUserId()));
        if (reportedUser.getId().equals(submitter.getId()))
            throw new ForbiddenOperationException("You cannot report yourself");
        ReportEntity reportEntity = reportRepository.save(new ReportEntity(submitter, reportedUser, submitReportRequest.getReason().toString(),
                submitReportRequest.getDetails()));
        return ReportModel.of(reportEntity);
    }

    public List<ReportModel> getBySubmitterId(long submitterId){
        return reportRepository.findBySubmitterId(submitterId).stream().map(ReportModel::of).toList();
    }

    public List<ReportModel> getByReportedUserId(long userId){
        return reportRepository.findByReportedUserId(userId).stream().map(ReportModel::of).toList();
    }

    @Transactional
    public ReportModel updateReport(long reportId, UpdateReportRequest updateReportRequest){
        ReportEntity reportEntity = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("No report found with id: " + reportId));
        if (reportEntity.getSubmitter().getId() == idCheckerService.getCurrentUserId())
            throw new ForbiddenOperationException("You cannot perform this operation.");
        reportEntity.setReason(updateReportRequest.getReason().toString());
        reportEntity.setDetails(updateReportRequest.getDetails());
        return ReportModel.of(reportEntity);
    }

    @Transactional
    public void deleteReport(long reportId){
        ReportEntity reportEntity = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("No report found with id: " + reportId));
        if (reportEntity.getSubmitter().getId() == idCheckerService.getCurrentUserId())
            throw new ForbiddenOperationException("You cannot perform this operation.");
        reportRepository.delete(reportEntity);
    }

    @Transactional
    public void deleteReportAdmin(long reportId){
        ReportEntity reportEntity = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("No report found with id: " + reportId));
        reportRepository.delete(reportEntity);
    }
}
