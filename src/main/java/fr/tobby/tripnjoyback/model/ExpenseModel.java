package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ExpenseEntity;
import fr.tobby.tripnjoyback.entity.ExpenseMemberEntity;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
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
    private GroupMemberModel purchaser;
    private Date date;
    private String icon;
    private List<ExpenseMemberModel> indebtedUsers;

    public static ExpenseModel of(ExpenseEntity expenseEntity, List<ExpenseMemberEntity> expenseMemberEntities) {
        return ExpenseModel.builder()
                .id(expenseEntity.getId())
                .total(expenseEntity.getTotal())
                .description(expenseEntity.getDescription())
                .date(expenseEntity.getDate())
                .purchaser(GroupMemberModel.of(expenseEntity.getPurchaser()))
                .groupModel(GroupModel.of(expenseEntity.getGroup()))
                .icon(expenseEntity.getIcon())
                .indebtedUsers(expenseMemberEntities.stream().map(ExpenseMemberModel::of).toList())
                .build();
    }
}
