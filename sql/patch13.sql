alter table users_groups
    drop constraint users_groups_group_id_fkey;

alter table users_groups
    add constraint users_groups_group_id_fkey
        foreign key (group_id) references groups
            on update cascade on delete cascade;
