package fr.tobby.tripnjoyback.entity.messaging;

import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.VoteEntity;
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
    @Setter
    private Date modifiedDate;
    @Column(name = "is_multiple_choice_survey")
    @Setter
    private boolean isMultipleChoiceSurvey;

    public SurveyEntity(UserEntity submitter, ChannelEntity channel, String question, boolean quizz, Date sendDate, Date modifiedDate, boolean canBeAnsweredMultipleTimes) {
        this.submitter = submitter;
        this.channel = channel;
        this.question = question;
        this.quizz = quizz;
        this.sendDate = sendDate;
        this.modifiedDate = modifiedDate;
        this.isMultipleChoiceSurvey = canBeAnsweredMultipleTimes;
        answers = new ArrayList<>();
        votes = new ArrayList<>();
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private Collection<SurveyAnswerEntity> answers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private Collection<VoteEntity> votes;
}
