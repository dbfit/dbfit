CONNECT TO DBFIT

CREATE TABLE DFTEST.USERS ( USERID BIGINT  NOT NULL  GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE ) , NAME VARCHAR (20)  , USERNAME VARCHAR (20)  NOT NULL   )

create function concatenatef(firststring VARCHAR(100), secondstring varchar(100)) returns varchar(4000) return firststring || secondstring

CREATE PROCEDURE calclength (IN name VARCHAR(400), OUT strlength INTEGER) LANGUAGE SQL NO EXTERNAL ACTION BEGIN set strlength =length(name); RETURN; END

CREATE PROCEDURE concatenatestrings (IN firststring VARCHAR(400), IN secondstring VARCHAR(400),  OUT concatenated varchar(400)) LANGUAGE SQL NO EXTERNAL ACTION BEGIN set concatenated =concat(firststring, concat(' ',secondstring)); RETURN; END

CREATE PROCEDURE makeuser LANGUAGE SQL BEGIN insert into users (name, username) values ('user1','fromproc'); RETURN; END

CREATE PROCEDURE multiplyio (IN factor int, INOUT val int)  LANGUAGE SQL BEGIN set val=val*factor; RETURN; END

CREATE function multiply(n1 int, n2 int) returns int return n1 * n2

CREATE PROCEDURE raise_error_with_params (IN name VARCHAR(200), OUT strlength INTEGER) LANGUAGE SQL BEGIN IF (name = 'xx') THEN SIGNAL SQLSTATE '20001' SET MESSAGE_TEXT = 'text exception'; END IF; SET strlength = LENGTH(name); RETURN; END

CREATE PROCEDURE raise_error_no_params LANGUAGE SQL BEGIN SIGNAL SQLSTATE '20001' SET MESSAGE_TEXT = 'text exception'; RETURN; END

-- Or do we use SIGNAL SQLSTATE '20000' SET MESSAGE_TEXT = 'text exception'; 
CONNECT RESET
