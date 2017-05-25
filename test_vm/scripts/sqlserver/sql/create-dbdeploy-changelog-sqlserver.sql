IF NOT EXISTS (SELECT * FROM DbFit.information_schema.tables WHERE table_schema = 'dbo' AND table_name = 'changelog')
    CREATE TABLE DbFit.dbo.changelog
    (
    change_number   INTEGER NOT NULL,
    complete_dt     DATETIME NOT NULL,
    applied_by      VARCHAR(100) NOT NULL,
    description     VARCHAR(500) NOT NULL,
    CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
    )
GO
