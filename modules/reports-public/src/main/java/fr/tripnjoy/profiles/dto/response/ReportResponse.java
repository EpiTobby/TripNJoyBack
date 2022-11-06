package fr.tripnjoy.profiles.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.profiles.model.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class ReportResponse {
    private final long id;
    @NotNull
    private final ReportReason reason;
    @Nullable
    private final String details;
    private final long reportedUser;
    private final long submitter;
    @NotNull
    private final Instant createdDate;
}
