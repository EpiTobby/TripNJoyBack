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
