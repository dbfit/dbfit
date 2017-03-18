CREATE TABLE IF NOT EXISTS changelog (
    change_number INTEGER CONSTRAINT Pkchangelog PRIMARY KEY,
    complete_dt TIMESTAMP NOT NULL,
    applied_by VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL
);

ALTER TABLE changelog OWNER to dbfit;
