CREATE DATABASE netezzatest
;

SET CATALOG netezzatest
;

CREATE TABLE changelog
(
  change_number   INTEGER NOT NULL,
  complete_dt     TIMESTAMP NOT NULL,
  applied_by      VARCHAR(100) NOT NULL,
  description     VARCHAR(500) NOT NULL
)
;
