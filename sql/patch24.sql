alter table expenses
    add purchaser_id int;

ALTER TABLE "expenses" ADD FOREIGN KEY ("purchaser_id") REFERENCES "users" ("id");

alter table expenses_members
    add id serial;

alter table expenses_members
    add constraint expenses_members_pk
        primary key (id);

alter table expenses_members rename column paid to amount_to_pay;