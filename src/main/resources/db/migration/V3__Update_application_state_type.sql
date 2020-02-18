drop table applications;
drop type application_state;

CREATE TYPE application_state AS ENUM('NEW','SENT','COMPLETED','EXPIRED','ACCEPTED','REJECTED');

create table applications (
    id serial PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    contact_info varchar(100),
    experience experience_level,
    application_state application_state default 'NEW'
);