package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class ExpenseMemberModel {
    private final long userId;
    private final double amountToPay;
}
