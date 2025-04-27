CREATE
DATABASE QuarkusSocial;

CREATE TABLE users
(
    id   bigserial    not null primary key,
    name varchar(100) not null,
    age  integer      not null
)

CREATE TABEL posts
(
    id      bigserial    not null primary key,
    post_text            varchar(140) not null,
    dateTime            timestamp    not null,
    user_id             bigint       not null,
    foreign key (user_id) references users (id)
)