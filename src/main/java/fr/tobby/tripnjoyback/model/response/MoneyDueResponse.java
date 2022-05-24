package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Builder
@Getter
@JsonSerialize
@JsonAutoDetect
@NoArgsConstructor
public class MoneyDueResponse {
    @NotNull
    private GroupMemberModel user;
    private double total;
}
