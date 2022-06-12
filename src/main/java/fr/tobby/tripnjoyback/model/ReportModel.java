package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ReportEntity;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ReportModel {
    @NotNull
    private ReportReason reason;
    @Nullable
    private String details;
    @NotNull
    private GroupMemberModel reportedUser;
    @NotNull
    private GroupMemberModel submitter;

    public static ReportModel of(ReportEntity reportEntity){
        return ReportModel.builder()
                .reason(ReportReason.valueOf(reportEntity.getReason()))
                .submitter(GroupMemberModel.of(reportEntity.getSubmitter()))
                .reportedUser(GroupMemberModel.of(reportEntity.getReportedUser()))
                .build();
    }
}
