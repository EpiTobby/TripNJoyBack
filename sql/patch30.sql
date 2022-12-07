create table notifications
(
    id serial
        constraint notifications_pk
            primary key,
    user_id int not null
        constraint notifications_users_id_fk
            references users
            on update cascade on delete cascade,
    title varchar,
    body varchar,
    firebase_id varchar
);