package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "report")
@Entity
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private UserEntity submitter;

    @ManyToOne
    @JoinColumn(name = "reported_id")
    private UserEntity reported;

    private String reason;

    private String details;

    public ReportEntity(UserEntity submitter, UserEntity reported, String reason, String details) {
        this.submitter = submitter;
        this.reported = reported;
        this.reason = reason;
        this.details = details;
    }
}
