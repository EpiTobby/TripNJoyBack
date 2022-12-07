alter table answers drop constraint answers_survey_id_fkey;

alter table answers
    add constraint answers_survey_id_fkey
        foreign key (survey_id) references surveys
            on delete cascade;