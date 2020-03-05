create table cv_urls (
    object_key varchar(100) PRIMARY KEY UNIQUE,
	url_string varchar(1000) NOT NUll,
	expiration bigint NOT NULL
);