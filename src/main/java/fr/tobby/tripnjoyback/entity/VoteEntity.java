package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "survey_vote")
@NoArgsConstructor
@Getter
public class VoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    @JoinColumn(name = "survey_id")
    private SurveyEntity survey;

    @ManyToOne
    @Setter
    @JoinColumn(name = "answer_id")
    private SurveyAnswerEntity answer;

    @ManyToOne
    @Setter
    @JoinColumn(name = "voter_id")
    private UserEntity voter;

    public VoteEntity(SurveyEntity survey, SurveyAnswerEntity answer, UserEntity voter) {
        this.survey = survey;
        this.answer = answer;
        this.voter = voter;
    }
}
