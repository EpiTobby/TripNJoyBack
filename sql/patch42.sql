create table users_match_tasks
(
	user_id int
		constraint users_match_tasks_pk
			primary key
		constraint users_match_tasks_users_id_fk
			references users
				on update cascade on delete cascade,
);

alter table users drop column waiting_for_group;


alter table activities_members drop constraint activities_members_participant_id_fkey;

alter table activities_members
    add constraint activities_members_users_id_fk
        foreign key (participant_id) references users
            on update cascade on delete cascade;


alter table expenses_members
    add constraint expenses_members_pk
        primary key (expense_id, user_id);


INSERT INTO message_type (id, name) VALUES (5, 'CALL')