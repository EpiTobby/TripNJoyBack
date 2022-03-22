create table roles
(
    id serial
        constraint roles_pk
        primary key,
    name varchar(8) not null
);

create table user_roles
(
    user_id int not null
        constraint user_roles_users_id_fk
            references users
            on delete cascade,
    role_id int not null
        constraint user_roles_roles_id_fk
            references roles
            on delete cascade,
    constraint user_roles_pk
        primary key (user_id, role_id)
);
