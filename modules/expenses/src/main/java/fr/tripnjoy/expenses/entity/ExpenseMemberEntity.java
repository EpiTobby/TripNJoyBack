package fr.tripnjoy.expenses.entity;

import fr.tripnjoy.expenses.dto.response.DebtDetailsResponse;
import fr.tripnjoy.expenses.dto.response.ExpenseMemberModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expenses_members")
@Entity
public class ExpenseMemberEntity {
    @EmbeddedId
    private Ids ids;

    @Column(name = "amount_to_pay")
    private double amountToPay;

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Embeddable
    public static class Ids implements Serializable {

        @ManyToOne
        private ExpenseEntity expense;

        private long userId;
    }

    public ExpenseMemberModel toModel()
    {
        return new ExpenseMemberModel(ids.getUserId(), amountToPay);
    }

    public DebtDetailsResponse toDebtModel()
    {
        return new DebtDetailsResponse(ids.getUserId(),
                getIds().getExpense().getPurchaserId(),
                getIds().getExpense().getDescription(),
                amountToPay,
                getIds().getExpense().getDate());
    }
}
