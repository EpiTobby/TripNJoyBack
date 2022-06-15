package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class SubmitReportRequest {

    @NotNull
    private long reportedUserId;
    @NotNull
    private ReportReason reason;
    @NotNull
    private String details;
}
