package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class ExpenseModel {
    private final long id;
    private final String description;
    private final double total;
    private final long groupId;
    private final long purchaserId;
    private final Date date;
    private final String icon;
    private final List<ExpenseMemberModel> indebtedUsers;
}
