alter table channels drop constraint channels_group_id_fkey;

alter table channels
    add constraint channels_group_id_fkey
        foreign key (group_id) references groups
            on delete cascade;

alter table messages drop constraint messages_channel_id_fkey;

alter table messages
    add constraint messages_channel_id_fkey
        foreign key (channel_id) references channels
            on delete cascade;

alter table messages drop constraint messages_user_id_fkey;

alter table messages
    add constraint messages_user_id_fkey
        foreign key (user_id) references users
            on delete set null;