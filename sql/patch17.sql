create table message_type
(
    id   serial
        constraint message_type_pk
            primary key,
    name varchar not null
);

INSERT INTO public.message_type (id, name)
VALUES (1, 'TEXT');
INSERT INTO public.message_type (id, name)
VALUES (2, 'IMAGE');
INSERT INTO public.message_type (id, name)
VALUES (3, 'FILE');

alter table messages
    add type_id int default 1 not null;

alter table messages
    add constraint messages_message_type_id_fk
        foreign key (type_id) references message_type
            on update cascade on delete cascade;

