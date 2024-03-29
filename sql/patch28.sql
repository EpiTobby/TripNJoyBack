CREATE TABLE IF NOT EXISTS group_memories
(
    "id"   SERIAL PRIMARY KEY,
    group_id   int not null
        constraint group_memories_groups_id_fk
            references groups
            on update cascade on delete cascade,
    memory_url text not null,
    constraint group_memories_pk
        unique (group_id, memory_url)
);
