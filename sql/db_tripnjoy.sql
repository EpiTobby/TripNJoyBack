CREATE TABLE "users" (
                         "id" SERIAL PRIMARY KEY,
                         "first_name" varchar,
                         "last_name" varchar,
                         "password" text,
                         "email" varchar,
                         "gender_id" int,
                         "birthdate" varchar,
                         "profile_picture" text,
                         "phone_number" varchar,
                         "city_id" int,
                         "created_date" timestamp
);

CREATE TABLE "genders" (
                           "id" SERIAL PRIMARY KEY,
                           "value" varchar
);

CREATE TABLE "groups" (
                          "id" SERIAL PRIMARY KEY,
                          "name" varchar,
                          "state_id" int,
                          "owner_id" int,
                          "max_size" int,
                          "created_date" timestamp,
                          "destination_id" int,
                          "start_of_trip" timestamp,
                          "end_of_trip" timestamp
);

CREATE TABLE "cities" (
                          "id" SERIAL PRIMARY KEY,
                          "name" varchar
);

CREATE TABLE "states" (
                          "id" SERIAL PRIMARY KEY,
                          "value" varchar
);

CREATE TABLE "users_groups" (
                                "user_id" int,
                                "group_id" int,
                                "profile_id" int
);

CREATE TABLE "profiles" (
                            "id" SERIAL PRIMARY KEY,
                            "user_id" int
);

CREATE TABLE "profile_criteria" (
                                    "profile_id" int,
                                    "question_id" int,
                                    "answer" text
);

CREATE TABLE "questions" (
                             "id" SERIAL PRIMARY KEY,
                             "value" varchar
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

ALTER TABLE "users" ADD FOREIGN KEY ("gender_id") REFERENCES "genders" ("id");

ALTER TABLE "users_groups" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "users_groups" ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id");

ALTER TABLE "groups" ADD FOREIGN KEY ("owner_id") REFERENCES "users" ("id");

ALTER TABLE "groups" ADD FOREIGN KEY ("state_id") REFERENCES "states" ("id");

ALTER TABLE "users" ADD FOREIGN KEY ("city_id") REFERENCES "cities" ("id");

ALTER TABLE "groups" ADD FOREIGN KEY ("destination_id") REFERENCES "cities" ("id");

ALTER TABLE "profiles" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "profile_criteria" ADD FOREIGN KEY ("profile_id") REFERENCES "profiles" ("id");

ALTER TABLE "profile_criteria" ADD FOREIGN KEY ("question_id") REFERENCES "questions" ("id");

ALTER TABLE "users_groups" ADD FOREIGN KEY ("profile_id") REFERENCES "profiles" ("id");

ALTER TABLE "reviews" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "reviews" ADD FOREIGN KEY ("reviewer_id") REFERENCES "users" ("id");

ALTER TABLE "report" ADD FOREIGN KEY ("submitter_id") REFERENCES "users" ("id");

ALTER TABLE "report" ADD FOREIGN KEY ("reported_id") REFERENCES "users" ("id");

ALTER TABLE "activities" ADD FOREIGN KEY ("group_id") REFERENCES "groups" ("id");

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

ALTER TABLE "suggestions" ADD FOREIGN KEY ("city_id") REFERENCES "cities" ("id");

INSERT INTO "genders" VALUES (0,"male");

INSERT INTO "genders" VALUES (1,"female");

INSERT INTO "genders" VALUES (2,"other");
