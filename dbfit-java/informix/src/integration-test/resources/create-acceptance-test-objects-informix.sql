CREATE TABLE Users (UserId SERIAL NOT NULL, Name VARCHAR(20), UserName VARCHAR(20) NOT NULL);

CREATE FUNCTION ConcatenateF(FirstString VARCHAR(100), SecondString VARCHAR(100)) RETURNS VARCHAR(200) RETURN FirstString || SecondString; END FUNCTION;

CREATE FUNCTION FuncWithOutParams(InString VARCHAR(100), OUT OutString VARCHAR(200)) RETURNS VARCHAR(200) LET OutString = InString || ' returned via OUT param'; RETURN InString || ' returned via RETURNS'; END FUNCTION;

CREATE PROCEDURE CalcLength(InString VARCHAR(255), OUT StrLength INTEGER) LET StrLength = LENGTH(InString); RETURN; END PROCEDURE;

CREATE PROCEDURE ConcatenateStrings(FirstString VARCHAR(255), SecondString VARCHAR(255), OUT Concatenated VARCHAR(255)) LET Concatenated = FirstString || ' ' || SecondString; RETURN; END PROCEDURE;

CREATE PROCEDURE MakeUser() INSERT INTO Users (Name, UserName) VALUES ('user1', 'fromproc'); END PROCEDURE;

CREATE PROCEDURE Multiply(Factor INTEGER, INOUT Val INTEGER) LET Val = Val * Factor; RETURN; END PROCEDURE;
