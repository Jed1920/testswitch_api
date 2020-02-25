create table applications (
    id serial PRIMARY KEY UNIQUE,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    contact_info varchar(100),
    experience varchar(50)
);