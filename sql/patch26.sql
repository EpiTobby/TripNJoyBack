DROP TABLE reviews;

CREATE TABLE "recommendations" (
                                   "id" SERIAL PRIMARY KEY,
                                   "recommended_user_id" int,
                                   "reviewer_id" int,
                                   "comment" text
);

ALTER TABLE "recommendations"
    ADD FOREIGN KEY ("recommended_user_id") REFERENCES "users" ("id");

ALTER TABLE "recommendations"
    ADD FOREIGN KEY ("reviewer_id") REFERENCES "users" ("id");