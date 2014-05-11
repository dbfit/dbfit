CONNECT TO DBFIT

CREATE TABLE DFTEST.USERS ( USERID BIGINT  NOT NULL  GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE ) , NAME VARCHAR (20)  , USERNAME VARCHAR (20)  NOT NULL   ) 

create function concatenatef(firststring VARCHAR(100), secondstring varchar(100)) returns varchar(4000) return firststring || secondstring

CREATE PROCEDURE calclength (IN name VARCHAR(400), OUT strlength INTEGER) LANGUAGE SQL NO EXTERNAL ACTION BEGIN set strlength =length(name); RETURN; END

CREATE PROCEDURE concatenatestrings (IN firststring VARCHAR(400), IN secondstring VARCHAR(400),  OUT concatenated varchar(400)) LANGUAGE SQL NO EXTERNAL ACTION BEGIN set concatenated =concat(firststring, concat(' ',secondstring)); RETURN; END

CREATE PROCEDURE makeuser LANGUAGE SQL BEGIN insert into users (name, username) values ('user1','fromproc'); RETURN; END

CREATE PROCEDURE multiply (IN factor int, INOUT val int)  LANGUAGE SQL BEGIN set val=val*factor; RETURN; END

CONNECT RESET
