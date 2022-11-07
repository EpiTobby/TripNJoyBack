package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class BalanceResponse {
    private final long user;
    private final double money;
}
