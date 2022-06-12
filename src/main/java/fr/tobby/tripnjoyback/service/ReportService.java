package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ReportEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ReportNotFoundException;
import fr.tobby.tripnjoyback.model.ReportModel;
import fr.tobby.tripnjoyback.model.request.SubmitReportRequest;
import fr.tobby.tripnjoyback.repository.ReportRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public ReportModel submitReport(long submitterId, SubmitReportRequest submitReportRequest){
        UserEntity submitter = userRepository.getById(submitterId);
        UserEntity reportedUser =  userRepository.getById(submitReportRequest.getReportedUserId());
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

    public void deleteReport(long reportId){
        ReportEntity reportEntity = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException("No report found with id: " + reportId));
        reportRepository.delete(reportEntity);
    }
}
