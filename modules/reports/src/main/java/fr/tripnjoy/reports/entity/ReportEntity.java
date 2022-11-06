package fr.tripnjoy.reports.entity;

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

    @Column(name = "submitter_id")
    private long submitter;

    @Column(name = "reported_id")
    private long reportedUser;

    @Setter
    private String reason;

    @Setter
    private String details;

    private Instant createdDate;

    public ReportEntity(long submitter, long reportedUser, String reason, String details, Instant createdDate) {
        this.submitter = submitter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.details = details;
        this.createdDate = createdDate;
    }
}
