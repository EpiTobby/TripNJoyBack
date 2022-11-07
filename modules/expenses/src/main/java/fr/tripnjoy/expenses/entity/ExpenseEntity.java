package fr.tripnjoy.expenses.entity;

import fr.tripnjoy.expenses.dto.response.ExpenseModel;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "expenses")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private double total;

    @Setter
    private String description;

    @Column(name = "purchaser_id")
    @Setter
    private long purchaserId;

    @Column(name = "group_id")
    private long groupId;

    @Column(name = "expense_date")
    private Date date;

    @Setter
    private String icon;

    public ExpenseModel toModel(List<ExpenseMemberEntity> expenseMemberEntities)
    {
        return new ExpenseModel(id,
                description,
                total,
                groupId,
                purchaserId,
                date,
                icon,
                expenseMemberEntities.stream().map(ExpenseMemberEntity::toModel).collect(Collectors.toList()));
    }
}
