CREATE TABLE "users" (
                         "id"                SERIAL PRIMARY KEY,
                         "first_name"        varchar,
                         "last_name"         varchar,
                         "password"          text,
                         "email"             varchar,
                         "gender_id"         int,
                         "birthdate"         varchar,
                         "profile_picture"   text,
                         "phone_number"      varchar,
                         "city_id"           int,
                         "created_date"      timestamp,
                         "confirmed"         boolean,
                         "waiting_for_group" boolean default false not null
);

CREATE TABLE "genders" (
                           "id" SERIAL PRIMARY KEY,
                           "value" varchar
);

CREATE TABLE "groups"(
                         "id"            SERIAL PRIMARY KEY,
                         "name"          varchar,
                         "state_id"      int,
                         "owner_id"      int,
                         "max_size"      int,
                         "created_date"  timestamp,
                         "start_of_trip" timestamp,
                         "end_of_trip"   timestamp,
                         "picture"       text
);

CREATE TABLE "cities" (
                          "id" SERIAL PRIMARY KEY,
                          "name" varchar
);

CREATE TABLE "states" (
                          "id" SERIAL PRIMARY KEY,
                          "value" varchar
);

CREATE TABLE "users_groups"
(
    "id"         SERIAL PRIMARY KEY,
    "user_id"    int,
    "group_id"   int,
    "profile_id" int,
    "pending"    bool
);

CREATE TABLE "profiles" (
                            "id" SERIAL PRIMARY KEY,
                            name varchar,
                            "active" bool
);

CREATE TABLE "reviews" (
                           "id" SERIAL PRIMARY KEY,
                           "user_id" int,
                           "reviewer_id" int,
                           "comment" text,
                           "grade" int
);

CREATE TABLE "report" (
                          "id" SERIAL PRIMARY KEY,
                          "submitter_id" int,
                          "reported_id" int,
                          "reason" text
);

CREATE TABLE "activities" (
                              "id" SERIAL PRIMARY KEY,
                              "group_id" int,
                              "name" varchar,
                              "begining" timestamp,
                              "end" timestamp,
                              "description" text
);

CREATE TABLE "activities_members" (
                                      "activity_id" int,
                                      "participant_id" int
);

CREATE TABLE "messages" (
                            "id" SERIAL PRIMARY KEY,
                            "user_id" int,
                            "channel_id" int,
                            "content" text,
                            "send_date" timestamp,
                            "modified_date" timestamp
);

CREATE TABLE "channels" (
                            "id" SERIAL PRIMARY KEY,
                            "group_id" int,
                            "name" varchar,
                            "index" int
);

CREATE TABLE "surveys" (
                           "id" SERIAL PRIMARY KEY,
                           "channel_id" int,
                           "submitter_id" int,
                           "question" text
);

CREATE TABLE "survey_answers" (
                                  "voter_id" int,
                                  "survey_id" int,
                                  "answer_id" int
);

CREATE TABLE "answers" (
                           "id" SERIAL PRIMARY KEY,
                           "content" varchar,
                           "survey_id" int
);

CREATE TABLE "expenses" (
                            "id" SERIAL PRIMARY KEY,
                            "total" float,
                            "group_id" int,
                            "description" text,
                            "expense_date" timestamp
);

CREATE TABLE "expenses_members" (
                                    "expense_id" int,
                                    "user_id" int,
                                    "paid" float
);

CREATE TABLE "suggestions" (
                               "id" SERIAL PRIMARY KEY,
                               "city_id" int,
                               "content" text
);

CREATE TABLE "confirmation_codes" (
                                      "id" SERIAL PRIMARY KEY,
                                      "user_id" int,
                                      "value" text,
                                      "expiration_date" timestamp
);

create table roles
(
    id serial
        constraint roles_pk
        primary key,
    name varchar(8) not null
);

create table users_roles
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

ALTER TABLE "users"
    ADD FOREIGN KEY ("gender_id") REFERENCES "genders" ("id");

ALTER TABLE "users_groups"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "users_groups"
    ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "groups"
    ADD FOREIGN KEY ("owner_id") REFERENCES "users" ("id");

ALTER TABLE "groups"
    ADD FOREIGN KEY ("state_id") REFERENCES "states" ("id");

ALTER TABLE "users"
    ADD FOREIGN KEY ("city_id") REFERENCES "cities" ("id");

ALTER TABLE "users_groups"
    ADD FOREIGN KEY ("profile_id") REFERENCES "profiles" ("id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "reviews"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "reviews"
    ADD FOREIGN KEY ("reviewer_id") REFERENCES "users" ("id");

ALTER TABLE "report"
    ADD FOREIGN KEY ("submitter_id") REFERENCES "users" ("id");

ALTER TABLE "report"
    ADD FOREIGN KEY ("reported_id") REFERENCES "users" ("id");

ALTER TABLE "activities"
    ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id");

ALTER TABLE "activities_members" ADD FOREIGN KEY ("activity_id") REFERENCES "activities" ("id");

ALTER TABLE "activities_members" ADD FOREIGN KEY ("participant_id") REFERENCES "users" ("id");

ALTER TABLE "messages" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "messages" ADD FOREIGN KEY ("channel_id") REFERENCES "channels" ("id");

ALTER TABLE "channels" ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id");

ALTER TABLE "survey_answers" ADD FOREIGN KEY ("survey_id") REFERENCES "surveys" ("id");

ALTER TABLE "answers" ADD FOREIGN KEY ("survey_id") REFERENCES "surveys" ("id");

ALTER TABLE "surveys" ADD FOREIGN KEY ("submitter_id") REFERENCES "users" ("id");

ALTER TABLE "surveys" ADD FOREIGN KEY ("channel_id") REFERENCES "channels" ("id");

ALTER TABLE "expenses_members" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "expenses" ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id");

ALTER TABLE "expenses_members" ADD FOREIGN KEY ("expense_id") REFERENCES "expenses" ("id");

ALTER TABLE "suggestions"
    ADD FOREIGN KEY ("city_id") REFERENCES "cities" ("id");

ALTER TABLE "confirmation_codes"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") on delete cascade;

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