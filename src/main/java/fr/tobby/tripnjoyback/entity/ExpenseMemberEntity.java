package fr.tobby.tripnjoyback.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expenses_members")
@Entity
public class ExpenseMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;

    @Column(name = "amount_to_pay")
    private double amountToPay;
}
