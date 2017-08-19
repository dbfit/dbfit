CREATE TABLE users
(
  name     VARCHAR(50)
, username VARCHAR(50)
, userid   NUMERIC
)
;

ET
;

REPLACE PROCEDURE ConcatenateStrings(IN firstString VARCHAR(20), IN secondString VARCHAR(20), OUT concatenated VARCHAR(40))
BEGIN
    SET concatenated = firstString || ' ' || secondString;
END;
;

ET
;

REPLACE PROCEDURE nulls_back(OUT out1 VARCHAR, OUT out2 VARCHAR, OUT out3 VARCHAR)
BEGIN
    SET out1 = '1';
    SET out2 = '1';
    SET out3 = '1';
END;
;

ET
;

REPLACE PROCEDURE ConcatenateStrings(IN firstString VARCHAR, IN secondString VARCHAR, OUT concatenated VARCHAR)
BEGIN
    SET concatenated = firstString || ' ' || secondString;
END;
;

ET
;

REPLACE FUNCTION ConcatenateF(firstString VARCHAR, secondString VARCHAR)
RETURNS VARCHAR
LANGUAGE SQL
CONTAINS SQL
DETERMINISTIC
COLLATION INVOKER
INLINE TYPE 1
    RETURN firstString || ' ' || secondString
;

ET
;

REPLACE PROCEDURE CalcLength(IN name VARCHAR(100), OUT strlength BIGINT)
BEGIN
    SET strlength = CHARACTERS(name);
END;
;

ET
;

REPLACE PROCEDURE MultiplyIO(IN factor NUMBER, INOUT val NUMBER)
BEGIN
    SET val = val * factor;
END;
;

ET
;

REPLACE PROCEDURE TestProc1(IN name VARCHAR(100), OUT strlength NUMBER)
BEGIN
    -- SQLSTATE 22012 maps to Teradata error number 2618.
    DECLARE my_exception_condition CONDITION FOR SQLSTATE '22012';
    DECLARE x INTEGER;
    IF (name = 'xx')
    THEN
        SIGNAL my_exception_condition;
    END IF;
    SET strlength = CHARACTERS(name);
END;
;

ET
;

REPLACE PROCEDURE makeuser()
BEGIN
    INSERT INTO users(name, username) VALUES('user1', 'fromproc');
END;
;

ET
;
