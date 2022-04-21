alter table users_groups
    add id serial;

alter table users_groups
    add constraint users_groups_pk
        primary key (id);
