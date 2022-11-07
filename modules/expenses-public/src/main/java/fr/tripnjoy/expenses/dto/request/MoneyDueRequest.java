package fr.tripnjoy.expenses.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@JsonAutoDetect
public class MoneyDueRequest {
    private final long userId;
    @Nullable
    private final Double money;

    public MoneyDueRequest(@JsonProperty("userId") final long userId, @Nullable @JsonProperty("money") final Double money)
    {
        this.userId = userId;
        this.money = money;
    }
}
