DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2020-01-30 10:00', 'USER breakfast', 500, 100000),
       ('2020-01-30 13:00', 'USER lunch', 1000, 100000),
       ('2020-01-30 20:00', 'USER dinner', 500, 100000),
       ('2020-02-01 20:00', 'USER dinner', 500, 100000),
       ('2020-01-31 00:00', 'ADMIN boundary meal', 100, 100001),
       ('2020-01-31 10:00', 'ADMIN breakfast', 1000, 100001),
       ('2020-01-31 13:00', 'ADMIN lunch', 500, 100001),
       ('2020-01-31 20:00', 'ADMIN dinner', 410, 100001);
