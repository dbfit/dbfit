CREATE TABLE data_types
(
  a_bigint                BIGINT
, a_bit                   BIT
, a_char                  CHAR(1)
, a_date                  DATE
, a_decimal               DECIMAL
, a_double                DOUBLE
, a_integer               INTEGER
, a_money                 MONEY
, a_numeric               NUMERIC
, a_real                  REAL
, a_smallint              SMALLINT
, a_smallmoney            SMALLMONEY
, a_text                  TEXT
, a_time                  TIME
, a_tinyint               TINYINT
, a_unsigned_bigint       UNSIGNED BIGINT
, a_unsigned_int          UNSIGNED INT
, a_varchar               VARCHAR(1)
, a_timestamp             TIMESTAMP
, a_datetime              DATETIME
, a_smalldatetime         SMALLDATETIME
)
GO

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

CREATE PROCEDURE CalcLength
(
  @name          VARCHAR(50)
, OUT @strlength NUMERIC
)
AS
BEGIN
    SELECT @strlength = length(@name);
END;
GO

CREATE FUNCTION Multiply
(
  @n1 INT
, @n2 INT
)
RETURNS INT
AS
    RETURN @n1 * @n2;
GO

CREATE PROCEDURE MultiplyIO
(
  @factor NUMERIC
, OUT @val NUMERIC
)
AS
BEGIN
    SELECT @val = @val * @factor;
END;
GO
