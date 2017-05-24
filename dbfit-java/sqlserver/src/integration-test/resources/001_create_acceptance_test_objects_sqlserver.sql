USE FitNesseTestDB
GO

CREATE FUNCTION dbo.Multiply
(
    @n1 INT
  , @n2 INT
)
RETURNS INT
AS
BEGIN
    DECLARE @num3 INT;
    SET @num3 = @n1 * @n2;
    RETURN @num3;
END;
GO

CREATE PROCEDURE [dbo].[CalcLength_P]
    @name        VARCHAR(255)
  , @strlength   INT OUTPUT
AS
BEGIN
    SET @strlength = DataLength(@name);
END;
GO

CREATE PROCEDURE [dbo].[Increment_P]
    @counter INT OUTPUT
AS
BEGIN
    SET @counter = ISNULL(@counter, 1) + 1;
END;
GO

CREATE FUNCTION [dbo].[ReturnUserTable_F]
(
    @howmuch int
)
RETURNS @userTable TABLE (
                         -- Add the column definitions for the TABLE variable here
                         [user] VARCHAR(50)
                       , [username] VARCHAR(255)
                         )
AS
BEGIN
    -- Fill the table variable with the rows for your result set
    DECLARE @i INT;
    SET @i = 0;
    WHILE (@i < @howmuch)
        BEGIN
            SET @i = @i + 1
            INSERT @userTable
                   (
                   [user]
                 , [username]
                   )
            VALUES (
                   'User ' + CAST(@i AS VARCHAR(10)), 'Username ' + CAST(@i AS VARCHAR(10))
                   );
        END;
    RETURN;
END;
GO

CREATE PROCEDURE [dbo].[ConcatenateStrings_P]
    @firstString  VARCHAR(255)
  , @secondString VARCHAR(255)
  , @concatenated VARCHAR(600) OUTPUT
AS
BEGIN
    SET @concatenated = @firstString + ' ' + @secondString;
END;
GO

CREATE FUNCTION [dbo].[ConcatenateStrings_F]
(
    @firstString  VARCHAR(255)
  , @secondString VARCHAR(255)
)
RETURNS VARCHAR(600)
AS
BEGIN
    DECLARE @concatenated VARCHAR(600);
    SET @concatenated = @firstString + ' ' + @secondString;
    RETURN @concatenated;
END;
GO

CREATE TABLE [dbo].[users] (
  [Name]          [VARCHAR](50) NULL
, [UserName]      [VARCHAR](50) NULL
, [UserId]        [INT] IDENTITY(1,1) NOT NULL
);
GO

CREATE PROCEDURE [dbo].[PopulateUserTable_P]
    @howmuch INT
AS
BEGIN
    -- Fill the table variable with the rows for your result set
    DECLARE @i INT;
    SET @i = 0;
    WHILE (@i < @howmuch)
        BEGIN
            SET @i = @i + 1;
            INSERT [users]
                   (
                   [Name]
                 , [UserName]
                   )
            VALUES (
                   'User ' + CAST(@i AS VARCHAR(10))
                 , 'Username ' + CAST(@i AS VARCHAR(10))
                   )
        END;
END;
GO

CREATE PROCEDURE [dbo].[OpenCrsr_P]
    @howmuch INT
  , @OutCrsr CURSOR VARYING OUTPUT
AS
BEGIN
    SET @OutCrsr = CURSOR FOR
                       SELECT TOP(@howmuch)
                              [Name]
                            , [UserName]
                            , [UserId]
                         FROM [users];

    OPEN @OutCrsr;
END;
GO

CREATE PROCEDURE [dbo].[DeleteUserTable_P]
AS
    DELETE [users];
GO

CREATE PROCEDURE [dbo].[TestProc2]
    @iddocument INT
  , @iddestination_user INT
AS
    DECLARE @errorsave INT;
    SET @errorsave = 0

    IF (@iddocument < 100)
        BEGIN
            SET @errorsave = 53120;
            raiserror(@errorsave, 15, 1, 'Custom error message');
            RETURN @errorsave
        END;
GO

CREATE procedure [dbo].[ListUsers_P]
    @howmuch int
AS
BEGIN
    SELECT TOP(@howmuch) *
      FROM users
     ORDER
        BY UserId;
END;
GO

CREATE PROCEDURE MultiplyIO(@factor INT, @val INT OUTPUT)
AS
BEGIN
    SET @val = @factor * @val;
END;
GO

CREATE PROCEDURE TestDecimal
    @inParam           DECIMAL(15, 8)
  , @copyOfInParam     DECIMAL(15, 8) OUTPUT
  , @constOutParam     DECIMAL(15, 8) OUTPUT
AS
BEGIN
    SET @copyOfInParam = @inParam;
    SET @constOutParam = 123.456;
END;
GO

CREATE PROCEDURE [dbo].[MakeUser]
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

USE FitNesseTestDB2
GO

CREATE TABLE [dbo].[Users2]
(
  Name     VARCHAR(50) NULL
, UserName VARCHAR(50) NULL
, UserId   INT IDENTITY(1,1) NOT NULL
);
GO

CREATE PROCEDURE dbo.MakeUser2
AS
BEGIN
    INSERT
      INTO Users2
           (
           Name
         , UserName
           )
    VALUES (
           'user1'
         , 'fromproc'
           )
    ;
END;
GO

-- Change the current database back to the dbdeploy container so that the
-- changelog can be updated.
USE DbFit
GO
