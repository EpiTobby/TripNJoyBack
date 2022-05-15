alter table activities drop constraint activities_group_id_fkey;

alter table activities
    add constraint activities_group_id_fkey
        foreign key (group_id) references groups
            on update cascade on delete cascade;

alter table activities_members drop constraint activities_members_activity_id_fkey;

alter table activities_members
    add constraint activities_members_activity_id_fkey
        foreign key (activity_id) references activities
            on update cascade on delete cascade;

alter table activities_members drop constraint activities_members_participant_id_fkey;

alter table activities_members
    add constraint activities_members_participant_id_fkey
        foreign key (participant_id) references users
            on update cascade on delete cascade;

alter table activities rename column begining to "start_date";

alter table activities rename column "end" to "end_date";

alter table activities_members drop constraint activities_members_participant_id_fkey;

alter table activities_members
    add constraint activities_members_participant_id_fkey
        foreign key (participant_id) references users_groups
            on update cascade on delete cascade;



