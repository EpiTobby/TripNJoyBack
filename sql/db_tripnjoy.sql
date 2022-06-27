CREATE TABLE IF NOT EXISTS "genders"
(
    "id"    SERIAL PRIMARY KEY,
    "value" varchar
);

CREATE TABLE IF NOT EXISTS "cities"
(
    "id"   SERIAL PRIMARY KEY,
    "name" varchar
);

CREATE TABLE IF NOT EXISTS languages
(
    id    SERIAL PRIMARY KEY,
    value varchar
);

CREATE TABLE IF NOT EXISTS "users" (
                         "id"                SERIAL PRIMARY KEY,
                         "first_name"        varchar,
                         "last_name"         varchar,
                         "password"          text,
                         "email"             varchar,
                         "gender_id"         int
                                   references genders (id),
                         "birthdate"         varchar,
                         "profile_picture"   text,
                         "phone_number"      varchar,
                         "city_id"           int
                                   references "cities",
                         "created_date"      timestamp,
                         "confirmed"         boolean,
                         "waiting_for_group" boolean default false not null,
                         "language_id"      int references languages
);

CREATE TABLE IF NOT EXISTS "states"
(
    "id"    SERIAL PRIMARY KEY,
    "value" varchar unique
);

CREATE TABLE IF NOT EXISTS "groups"(
                         "id"            SERIAL PRIMARY KEY,
                         "name"          varchar,
                         "description"   varchar,
                         "state_id"      int
                                   references states,
                         "owner_id"      int
                                   references users,
                         "max_size"      int,
                         "created_date"  timestamp,
                         "start_of_trip" timestamp,
                         "end_of_trip"   timestamp,
                         "picture"       text
);

CREATE TABLE IF NOT EXISTS "profiles" (
                                          "id" SERIAL PRIMARY KEY,
                                          name varchar,
                                          "active" bool
);

CREATE TABLE IF NOT EXISTS "users_groups"
(
    "id"         SERIAL PRIMARY KEY,
    "user_id"    int references users (id),
    "group_id"   int references groups (id),
    "profile_id" int references profiles (id),
    "pending"    bool
);

CREATE TABLE IF NOT EXISTS "recommandations" (
                           "id" SERIAL PRIMARY KEY,
                           "recommanded_user_id" int
                               references users (id),
                           "reviewer_id" int
                               references users (id),
                           "comment" text NULL
);

CREATE TABLE IF NOT EXISTS "report" (
                          "id" SERIAL PRIMARY KEY,
                          "submitter_id" int
                              references users (id),
                          "reported_id"  int
                              references users (id),
                          "reason" text,
                          details text
);

CREATE TABLE IF NOT EXISTS "activities" (
                              "id" SERIAL PRIMARY KEY,
                              "group_id" int references groups,
                              "name" varchar,
                              "start_date" timestamp,
                              "end_date" timestamp,
                              "description" text,
                              "color" varchar,
                              "location" text,
                              "icon" text
);

CREATE TABLE IF NOT EXISTS "activities_members" (
                                      "activity_id" int references activities,
                                      "participant_id" int references users_groups
);

CREATE TABLE IF NOT EXISTS "channels" (
                                          "id" SERIAL PRIMARY KEY,
                                          "group_id" int references groups,
                                          "name" varchar,
                                          "index" int
);

CREATE TABLE IF NOT EXISTS message_type
(
    id   serial
        constraint message_type_pk
            primary key,
    name varchar not null
);

CREATE TABLE IF NOT EXISTS "messages" (
                            "id" SERIAL PRIMARY KEY,
                            "user_id" int references users on delete set null,
                            "channel_id" int references channels,
                            "content" text,
                            "send_date" timestamp,
                            "modified_date" timestamp,
                            "type_id" int default 1 not null references message_type
);

CREATE TABLE IF NOT EXISTS "surveys" (
                           "id" SERIAL PRIMARY KEY,
                           "channel_id" int references channels,
                           "submitter_id" int references users,
                           "question" text
);

CREATE TABLE IF NOT EXISTS "survey_answers" (
                                  "voter_id" int,
                                  "survey_id" int references surveys,
                                  "answer_id" int
);

CREATE TABLE IF NOT EXISTS "answers" (
                           "id" SERIAL PRIMARY KEY,
                           "content" varchar,
                           "survey_id" int references surveys
);

CREATE TABLE IF NOT EXISTS "expenses" (
                            "id" SERIAL PRIMARY KEY,
                            "total" float,
                            "group_id" int references groups,
                            "description" text,
                            purchaser_id int references users,
                            "expense_date" timestamp
);

CREATE TABLE IF NOT EXISTS "expenses_members" (
                                    "expense_id" int references expenses,
                                    "user_id" int references users,
                                    "paid" float
);

CREATE TABLE IF NOT EXISTS "suggestions" (
                               "id" SERIAL PRIMARY KEY,
                               "city_id" int references cities,
                               "content" text
);

CREATE TABLE IF NOT EXISTS "confirmation_codes" (
                                      "id" SERIAL PRIMARY KEY,
                                      "user_id" int references users,
                                      "value" text,
                                      "expiration_date" timestamp
);

CREATE TABLE IF NOT EXISTS roles
(
    id serial
        constraint roles_pk
        primary key,
    name varchar(8) not null
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id int not null
        constraint users_roles_users_id_fk
            references users
            on delete cascade,
    role_id int not null
        constraint users_roles_roles_id_fk
            references roles
            on delete cascade,
    constraint users_roles_pk
        primary key (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS user_profiles
(
    user_id    int
        constraint user_profiles_users_id_fk
            references users
            on update cascade on delete cascade,
    profile_id int
        constraint user_profiles_profiles_id_fk
            references profiles
            on update cascade on delete cascade,
    constraint user_profiles_pk
        primary key (user_id, profile_id)
);

CREATE TABLE IF NOT EXISTS group_profiles
(
    group_id   int not null
        constraint group_profiles_groups_id_fk
            references groups
            on update cascade on delete cascade,
    profile_id int not null
        constraint group_profiles_profiles_id_fk
            references profiles
            on update cascade on delete cascade,
    constraint group_profiles_pk
        primary key (group_id, profile_id)
);

INSERT INTO "genders" (value)
VALUES ('male');

INSERT INTO "genders" (value)
VALUES ('female');

INSERT INTO "genders" (value)
VALUES ('other');

INSERT INTO states
VALUES (0, 'OPEN');
INSERT INTO states
VALUES (1, 'CLOSED');
INSERT INTO states
VALUES (2, 'ARCHIVED');

INSERT INTO roles (name)
VALUES ('default');
INSERT INTO roles (name)
VALUES ('admin');

INSERT INTO languages VALUES (0,'ENGLISH');
INSERT INTO languages VALUES (1,'FRENCH');

INSERT INTO public.message_type (id, name)
VALUES (1, 'TEXT');
INSERT INTO public.message_type (id, name)
VALUES (2, 'IMAGE');
INSERT INTO public.message_type (id, name)
VALUES (3, 'FILE');

CREATE TABLE IF NOT EXISTS activities_info
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

