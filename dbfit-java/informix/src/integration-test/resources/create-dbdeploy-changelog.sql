CREATE TABLE IF NOT EXISTS changelog (
    change_number INTEGER NOT NULL,
    complete_dt DATETIME YEAR TO SECOND NOT NULL,
    applied_by VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (change_number)
);
