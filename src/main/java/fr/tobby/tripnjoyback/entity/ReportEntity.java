package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
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
    private UserEntity reportedUser;

    @Setter
    private String reason;

    @Setter
    private String details;

    private Instant createdDate;

    public ReportEntity(UserEntity submitter, UserEntity reportedUser, String reason, String details, Instant createdDate) {
        this.submitter = submitter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.details = details;
        this.createdDate = createdDate;
    }
}
