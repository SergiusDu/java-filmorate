DELETE
FROM film_genres;
DELETE
FROM friendships;
DELETE
FROM likes;
DELETE
FROM films;
DELETE
FROM users;
DELETE
FROM directors;
DELETE
FROM film_directors;

ALTER TABLE films
    ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users
    ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE directors
    ALTER COLUMN director_id RESTART WITH 1;