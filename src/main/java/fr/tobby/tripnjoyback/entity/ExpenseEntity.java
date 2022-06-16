package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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

    @ManyToOne
    @JoinColumn(name = "purchaser_id")
    private UserEntity purchaser;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "expense_date")
    private Date date;

    @Setter
    private String icon;
}
