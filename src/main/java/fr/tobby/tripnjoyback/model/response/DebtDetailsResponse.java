package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import fr.tobby.tripnjoyback.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@JsonSerialize
@JsonAutoDetect
@NoArgsConstructor
public class DebtDetailsResponse {
    private GroupMemberModel indebtedUser;
    private GroupMemberModel purchaser;
    private String description;
    private double amountToPay;
    private Date date;

    public static DebtDetailsResponse of(ExpenseMemberEntity expenseMemberEntity){
        return DebtDetailsResponse.builder()
                .indebtedUser(GroupMemberModel.of(expenseMemberEntity.getUser()))
                .purchaser(GroupMemberModel.of(expenseMemberEntity.getExpense().getPurchaser()))
                .description(expenseMemberEntity.getExpense().getDescription())
                .amountToPay(expenseMemberEntity.getAmountToPay())
                .date(expenseMemberEntity.getExpense().getDate())
                .build();
    }
}
