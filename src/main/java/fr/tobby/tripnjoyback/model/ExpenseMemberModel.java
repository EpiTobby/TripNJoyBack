package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ExpenseMemberModel {
    private long id;
    private UserModel userModel;
    private double amountToPay;

    public static ExpenseMemberModel of(ExpenseMemberEntity expenseMemberEntity){
        return ExpenseMemberModel.builder()
                .id(expenseMemberEntity.getId())
                .userModel(UserModel.of(expenseMemberEntity.getUser()))
                .amountToPay(expenseMemberEntity.getAmountToPay())
                .build();
    }
}
