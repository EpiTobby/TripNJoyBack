alter table report
    add created_date timestamp default now() not null;

alter table profiles
    add created_date timestamp default now() not null;
