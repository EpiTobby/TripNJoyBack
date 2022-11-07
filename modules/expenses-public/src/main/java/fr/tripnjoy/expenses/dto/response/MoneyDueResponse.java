package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class MoneyDueResponse {
    private final long userId;
    private final double total;
}
