alter table users
    add waiting_for_group boolean default false not null;