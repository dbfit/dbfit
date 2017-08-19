CREATE USER dftest FROM sysadmin AS PASSWORD = dftest SPOOL = 10000000 TEMPORARY = 1000000 PERMANENT = 1000000
;

GRANT CREATE FUNCTION ON dftest TO dftest
;

GRANT EXECUTE FUNCTION ON dftest TO dftest
;

GRANT CREATE PROCEDURE ON dftest TO dftest
;

GRANT EXECUTE PROCEDURE ON dftest TO dftest
;

DATABASE dftest
;

CREATE TABLE changelog
(
  change_number   INTEGER NOT NULL,
  complete_dt     TIMESTAMP NOT NULL,
  applied_by      VARCHAR(100) NOT NULL,
  description     VARCHAR(500) NOT NULL
)
;
