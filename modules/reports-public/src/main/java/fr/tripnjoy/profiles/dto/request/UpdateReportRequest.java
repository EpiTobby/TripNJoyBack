package fr.tripnjoy.profiles.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.profiles.model.ReportReason;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@JsonAutoDetect
public class UpdateReportRequest {
    @NotNull
    private final ReportReason reason;
    @NotNull
    private final String details;

    public UpdateReportRequest(@NotNull @JsonProperty("reason") final ReportReason reason, @NotNull @JsonProperty("details") final String details)
    {
        this.reason = reason;
        this.details = details;
    }
}
