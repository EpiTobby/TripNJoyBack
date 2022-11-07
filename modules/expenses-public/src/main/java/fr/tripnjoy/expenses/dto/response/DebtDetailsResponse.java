package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class DebtDetailsResponse {
    private final long indebtedUser;
    private final long purchaser;
    private final String description;
    private final double amountToPay;
    private final Date date;
}
