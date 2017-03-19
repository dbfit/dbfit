CREATE TABLE users (
  userid SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE,
  username VARCHAR(50)
)
;

CREATE FUNCTION ConcatenateStrings (firststring varchar(100), secondstring varchar(100))
RETURNS VARCHAR AS
$body$
BEGIN
  RETURN COALESCE(firststring, '')  || ' ' || COALESCE(secondstring, '');
END;
$body$
LANGUAGE plpgsql
;

CREATE FUNCTION CalcLength(name varchar(100)) RETURNS INTEGER AS
$body$
BEGIN
  RETURN LENGTH(name);
END;
$body$
LANGUAGE plpgsql
;

CREATE FUNCTION ConcatenateF (firststring VARCHAR(100), secondstring varchar(100))
RETURNS VARCHAR(200) AS
$body$
BEGIN
  RETURN COALESCE(firststring, '') || ' ' || COALESCE(secondstring, '');
END;
$body$
LANGUAGE plpgsql
;

CREATE FUNCTION makeuser() RETURNS VOID AS
$body$
BEGIN
  INSERT INTO users (name, username)
       VALUES ('user1','fromproc');
END;
$body$
LANGUAGE plpgsql
;

CREATE FUNCTION createuser(newname varchar(100), newusername varchar(100))
RETURNS VOID AS
$body$
BEGIN
  INSERT INTO users (name, username)
       VALUES (newname, newusername);
END;
$body$
LANGUAGE plpgsql
;

CREATE FUNCTION Multiply(n1 INTEGER, n2 INTEGER)
RETURNS INTEGER AS
$body$
BEGIN
  RETURN (n1 * n2);
END;
$body$
LANGUAGE plpgsql
;

CREATE OR REPLACE FUNCTION MultiplyIO(factor INTEGER, inout val INTEGER) AS
$body$
BEGIN
  val := val * factor;
END;
$body$
LANGUAGE plpgsql
;
