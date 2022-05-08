create table languages(
    id SERIAL PRIMARY KEY,
    value varchar
);

INSERT INTO languages VALUES (0,'ENGLISH');
INSERT INTO languages VALUES (1,'FRENCH');

alter table users add column language_id int;

alter table users add constraint users_language_id_fk
    foreign key (language_id) references languages(id);

