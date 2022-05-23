package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ExpenseModel {
    private long id;
    private String description;
    private double total;
    private GroupModel groupModel;
    private UserModel userModel;
    private Date date;
    private List<ExpenseMemberModel> indebtedMembers;

    public static ExpenseModel of(ExpenseEntity expenseEntity, List<ExpenseMemberEntity> expenseMemberEntities){
        return ExpenseModel.builder()
                .id(expenseEntity.getId())
                .total(expenseEntity.getTotal())
                .description(expenseEntity.getDescription())
                .date(expenseEntity.getDate())
                .userModel(UserModel.of(expenseEntity.getPurchaser()))
                .groupModel(GroupModel.of(expenseEntity.getGroupEntity()))
                .indebtedMembers(expenseMemberEntities.stream().map(ExpenseMemberModel::of).toList())
                .build();
    }
}
