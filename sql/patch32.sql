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