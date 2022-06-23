package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Builder
@Getter
@JsonAutoDetect
@NoArgsConstructor
public class MoneyDueRequest {
    private long userId;
    @Nullable
    private Double money;
}
