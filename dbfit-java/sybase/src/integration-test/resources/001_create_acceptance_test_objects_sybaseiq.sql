CREATE TABLE users
(
  Name          VARCHAR(50) NULL
, UserName      VARCHAR(50) NULL
, UserId        INT IDENTITY NOT NULL
)
GO

CREATE PROCEDURE MakeUser
AS
BEGIN
    INSERT
      INTO users
           (
           Name
         , UserName
           )
    VALUES (
           'user1'
         , 'fromproc'
           );
END;
GO
