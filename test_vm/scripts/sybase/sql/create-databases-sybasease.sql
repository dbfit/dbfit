CREATE DATABASE [DbFit]
GO

CREATE LOGIN [DbFit] WITH PASSWORD 'DbFit000!' DEFAULT DATABASE [DbFit]
GO

USE [DbFit]
GO

sp_adduser [DbFit], [DbFit]
GO

GRANT ROLE sa_role TO [DbFit]
GO

CREATE TABLE changelog (change_number INTEGER NOT NULL, complete_dt DATETIME NOT NULL, applied_by VARCHAR(100) NOT NULL, description VARCHAR(500) NOT NULL, CONSTRAINT Pkchangelog PRIMARY KEY (change_number))
GO

