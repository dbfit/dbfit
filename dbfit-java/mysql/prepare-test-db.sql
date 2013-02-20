drop database dbfit;

create database dbfit;

grant all privileges on dbfit.* to dftest@localhost identified by 'dftest';

grant all privileges on dbfit.* to dftest@127.0.0.1 identified by 'dftest';

grant all privileges on dbfit.* to dbfit_user@localhost identified by 'password';

grant all privileges on dbfit.* to dbfit_user@127.0.0.1 identified by 'password';

grant select on mysql.* to dbfit_user;

flush privileges;

use dbfit;

CREATE TABLE changelog (
  change_number INTEGER NOT NULL,
  complete_dt TIMESTAMP NOT NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
);

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number);
