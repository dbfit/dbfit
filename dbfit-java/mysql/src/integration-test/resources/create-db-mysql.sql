create database if not exists dbfit;
use dbfit;

CREATE TABLE IF NOT EXISTS changelog (
    change_number INTEGER NOT NULL,
    complete_dt TIMESTAMP NOT NULL,
    applied_by VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
);

create user 'dbfit_user'@'localhost' identified by 'password';
grant all privileges on dbfit.* to 'dbfit_user'@'localhost';
grant select on mysql.proc to 'dbfit_user'@'localhost';
