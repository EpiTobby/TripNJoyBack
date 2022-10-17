package fr.tobby.tripnjoyback.entity.messaging;

import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.VoteEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "surveys")
@NoArgsConstructor
@Getter
public class SurveyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private UserEntity submitter;
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelEntity channel;
    @Setter
    private String question;
    @Setter
    private boolean quizz;
    private Date sendDate;
    private Date modifiedDate;

    public SurveyEntity(UserEntity submitter, ChannelEntity channel, String question, boolean quizz, Date sendDate, Date modifiedDate) {
        this.submitter = submitter;
        this.channel = channel;
        this.question = question;
        this.quizz = quizz;
        this.sendDate = sendDate;
        this.modifiedDate = modifiedDate;
        answers = new ArrayList<>();
        votes = new ArrayList<>();
    }

    @OneToMany
    @JoinColumn(name = "survey_id")
    private Collection<SurveyAnswerEntity> answers;

    @OneToMany
    @JoinColumn(name = "survey_id")
    private Collection<VoteEntity> votes;
}
