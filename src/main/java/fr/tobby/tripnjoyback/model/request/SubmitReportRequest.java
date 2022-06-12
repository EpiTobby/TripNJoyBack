package fr.tobby.tripnjoyback.model.request;

import fr.tobby.tripnjoyback.model.ReportReason;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class SubmitReportRequest {

    @NotNull
    private long reportedUserId;
    @NotNull
    private ReportReason reason;
    @NotNull
    private String details;
}
