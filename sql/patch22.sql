create table activities_info
(
    id serial
        constraint activities_info_pk
        primary key,
    activity_id int not null
        constraint activities_info_activities_id_fk
        references activities
        on update cascade on delete cascade,
    content text not null
);

