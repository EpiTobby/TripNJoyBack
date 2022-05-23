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
public class DebtResponse {
    private UserModel indebtedUser;
    private UserModel purchaser;
    private String description;
    private double amountToPay;
    private Date date;

    public static DebtResponse of(ExpenseMemberEntity expenseMemberEntity){
        return DebtResponse.builder()
                .indebtedUser(UserModel.of(expenseMemberEntity.getUser()))
                .purchaser(UserModel.of(expenseMemberEntity.getExpense().getPurchaser()))
                .description(expenseMemberEntity.getExpense().getDescription())
                .amountToPay(expenseMemberEntity.getAmountToPay())
                .date(expenseMemberEntity.getExpense().getDate())
                .build();
    }
}
