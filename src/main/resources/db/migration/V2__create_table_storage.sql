create table storage (
    id serial primary key,
    date timestamp  not null,
    file_name varchar(100) not null,
    file_size integer not null,
    file_content bytea not null,
    user_id integer REFERENCES users (id),
    UNIQUE (file_name, user_id)
);