CREATE DATABASE [DbFit]
GO

CREATE LOGIN [DbFit] WITH PASSWORD = 'DbFit000!', DEFAULT_DATABASE = [DbFit]
GO

USE [DbFit]
GO

CREATE USER [DbFit] FOR LOGIN [DbFit] WITH DEFAULT_SCHEMA = [dbo]
GO

ALTER ROLE [db_owner] ADD MEMBER [DbFit]
GO

ALTER DATABASE [DbFit] SET READ_WRITE
GO

CREATE DATABASE [FitNesseTestDB]
GO

CREATE DATABASE [FitNesseTestDB2]
GO

/* Case sensitive collation - for additional required precision for testing */
ALTER DATABASE [FitNesseTestDB] COLLATE Latin1_General_CS_AS
GO

/* Case sensitive collation - for additional required precision for testing */
ALTER DATABASE [FitNesseTestDB2] COLLATE Latin1_General_CS_AS
GO

CREATE LOGIN [FitNesseUser] WITH PASSWORD = 'FitN355#'
GO

USE [FitNesseTestDB]
GO

CREATE USER [DbFit] FOR LOGIN [DbFit] WITH DEFAULT_SCHEMA = [dbo]
GO

ALTER ROLE [db_owner] ADD MEMBER [DbFit]
GO

CREATE USER [FitNesseUser] FOR LOGIN [FitNesseUser] WITH DEFAULT_SCHEMA = [dbo]
GO

ALTER ROLE [db_owner] ADD MEMBER [FitNesseUser]
GO

ALTER DATABASE [FitNesseTestDB] SET READ_WRITE
GO

USE [FitNesseTestDB2]
GO

CREATE USER [DbFit] FOR LOGIN [DbFit] WITH DEFAULT_SCHEMA = [dbo]
GO

ALTER ROLE [db_owner] ADD MEMBER [DbFit]
GO

CREATE USER [FitNesseUser] FOR LOGIN [FitNesseUser] WITH DEFAULT_SCHEMA = [dbo]
GO

ALTER ROLE [db_owner] ADD MEMBER [FitNesseUser]
GO

ALTER DATABASE [FitNesseTestDB2] SET READ_WRITE
GO

sp_addmessage @msgnum = 53120, @severity=1, @msgtext = 'test user defined error msg';
GO
