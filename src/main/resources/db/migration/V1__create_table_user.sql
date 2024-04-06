create table users (
    id serial primary key,
    username varchar(50) not null,
    password varchar(255) not null
);

insert into users (id, username, password)
values (1, 'test', '$2a$12$y82hRaN/LDa35gegqpkgr.HlO6M3JLrjs1sQa8EBJp8.BGCYnzuXO'); -- test
insert into users (id, username, password)
values (2, 'mikechirkov', '$2a$12$MKZuK6GlbruD7F9ilYjpTOUwLFBtkg0v8Y7S2krPQmK1XVAU289GG'); -- mikechirkov