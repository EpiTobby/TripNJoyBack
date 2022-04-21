create table user_profiles
(
    user_id    int
        constraint user_profiles_users_id_fk
            references users
            on update cascade on delete cascade,
    profile_id int,
    constraint user_profiles_pk
        primary key (user_id, profile_id)
);

create table group_profiles
(
    group_id   int not null
        constraint group_profiles_groups_id_fk
            references groups
            on update cascade on delete cascade,
    profile_id int not null,
    constraint group_profiles_pk
        primary key (group_id, profile_id)
);

alter table user_profiles
    add constraint user_profiles_profiles_id_fk
        foreign key (profile_id) references profiles
            on update cascade on delete cascade;

alter table group_profiles
    add constraint group_profiles_profiles_id_fk
        foreign key (profile_id) references profiles
            on update cascade on delete cascade;

