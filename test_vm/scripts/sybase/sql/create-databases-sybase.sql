CREATE DATABASE '/var/sybaseiq_db/dbfit.db' DBA USER 'sa' DBA PASSWORD 'DbFit1' IQ PATH '/var/sybaseiq_db/dbfit.iq' IQ SIZE 512;

START DATABASE '/var/sybaseiq_db/dbfit.db' AUTOSTOP OFF;

CONNECT TO dbfit DATABASE dbfit USER "sa" IDENTIFIED BY DbFit1;

CREATE USER dbfit IDENTIFIED BY dbfituser;

GRANT RESOURCE TO dbfit;

CONNECT TO dbfit DATABASE dbfit USER "dbfit" IDENTIFIED BY dbfituser;

CREATE TABLE IF NOT EXISTS changelog (change_number INTEGER NOT NULL, complete_dt DATETIME NOT NULL, applied_by VARCHAR(100) NOT NULL, description  VARCHAR(500) NOT NULL, CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
);
