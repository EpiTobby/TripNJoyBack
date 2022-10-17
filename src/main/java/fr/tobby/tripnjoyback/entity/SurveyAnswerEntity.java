package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "answers")
@NoArgsConstructor
@Getter
public class SurveyAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String content;

    @ManyToOne
    @Setter
    @JoinColumn(name = "survey_id")
    private SurveyEntity survey;

    @Setter
    @Column(name = "right_answer")
    private boolean rightAnswer;
}
