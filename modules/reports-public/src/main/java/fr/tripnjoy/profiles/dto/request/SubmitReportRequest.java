package fr.tripnjoy.profiles.dto.request;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.profiles.model.ReportReason;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@JsonAutoDetect
public class SubmitReportRequest {

    private final long reportedUserId;
    @NotNull
    private final ReportReason reason;
    @NotNull
    private final String details;

    public SubmitReportRequest(@JsonProperty("reportedUserId") final long reportedUserId, @NotNull @JsonProperty("reason") final ReportReason reason,
                               @NotNull @JsonProperty("details") final String details)
    {
        this.reportedUserId = reportedUserId;
        this.reason = reason;
        this.details = details;
    }
}
