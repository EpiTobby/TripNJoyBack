package fr.tripnjoy.reports.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.profiles.dto.response.ReportResponse;
import fr.tripnjoy.profiles.model.ReportReason;
import fr.tripnjoy.reports.entity.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ReportModel {
    private long id;
    @NotNull
    private ReportReason reason;
    @Nullable
    private String details;
    private long reportedUser;
    private long submitter;
    @NotNull
    private Instant createdDate;

    public static ReportModel of(ReportEntity reportEntity){
        return ReportModel.builder()
                .id(reportEntity.getId())
                .reason(ReportReason.valueOf(reportEntity.getReason()))
                .details(reportEntity.getDetails())
                .submitter(reportEntity.getSubmitter())
                .reportedUser(reportEntity.getReportedUser())
                .createdDate(reportEntity.getCreatedDate())
                .build();
    }

    public ReportResponse toDtoResponse()
    {
        return new ReportResponse(id, toDtoResponse().getReason(), details, reportedUser, submitter, createdDate);
    }
}
