CREATE TABLE USERS (USERID SERIAL NOT NULL, NAME VARCHAR(20), USERNAME VARCHAR(20) NOT NULL);

CREATE FUNCTION concatenatef(firststring VARCHAR(100), secondstring VARCHAR(100)) RETURNS VARCHAR(100) return firststring || secondstring; END FUNCTION;

CREATE PROCEDURE calclength(instring VARCHAR(255), OUT strlength INTEGER) LET strlength = 1; RETURN; END PROCEDURE;

CREATE PROCEDURE concatenatestrings(firststring VARCHAR(255), secondstring VARCHAR(255), OUT concatenated VARCHAR(255)) LET concatenated = firststring || ' ' || secondstring; RETURN; END PROCEDURE;

CREATE PROCEDURE makeuser() INSERT INTO users (name, username) VALUES ('user1', 'fromproc'); END PROCEDURE;

CREATE PROCEDURE multiply(factor INTEGER, INOUT val INTEGER) LET val = val * factor; RETURN; END PROCEDURE;
