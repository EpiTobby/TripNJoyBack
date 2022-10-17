alter table surveys
    add quizz boolean;

alter table surveys
    add send_date timestamp;

alter table surveys
    add modified_date timestamp;

alter table answers
    add right_answer boolean;

INSERT INTO public.message_type (id, name)
VALUES (4, 'SURVEY');

alter table survey_answers rename to survey_vote;

alter table survey_vote drop constraint survey_answers_survey_id_fkey;

alter table survey_vote
    add constraint survey_answers_survey_id_fkey
        foreign key (survey_id) references surveys
            on delete cascade;

alter table survey_vote
    add constraint survey_answers_voter_id_fkey
        foreign key (voter_id) references users
            on delete cascade;

alter table survey_vote
    add constraint survey_answers_answer_id_fkey
        foreign key (answer_id) references answers
            on delete cascade;

alter table survey_vote
    add id int;

alter table survey_vote
    add constraint survey_vote_pk
        primary key (id);

create sequence survey_vote_id_seq;

alter table survey_vote alter column id set default nextval('public.survey_vote_id_seq');

alter sequence survey_vote_id_seq owned by survey_vote.id;