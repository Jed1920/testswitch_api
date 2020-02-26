CREATE TABLE sent_tests (
  id int NOT NULL,
  test_string VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_id FOREIGN KEY (id) REFERENCES applications (id)
);