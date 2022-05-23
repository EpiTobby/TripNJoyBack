package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name  = "expenses")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double total;

    private String description;

    @ManyToOne
    @JoinColumn(name = "purchaser_id")
    private UserEntity purchaser;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "expense_date")
    private Date date;
}
