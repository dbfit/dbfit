---
layout: page
title: Database-specific Information
nav_bar_name: docs
show_comments: true
---
## DB2

_Contributed by [Mike Patrick](https://github.com/mikepatrick)_

### Why are there multiple DB2 adapters?

There are essentially three flavors of DB2:

 * DB2 for LUW (Linux, Unix, Windows).
 * DB2 for System i.
 * DB2 for System z.

Each flavor comes with its own peculiarities.  There are driver differences:

 * DB2 for LUW and DB2 for System Z use IBM's native JDBC driver (`db2jcc.jar`).  On System z, a license file is required.
 * DB2 for System i most often uses IBM's Java Toolbox Driver (`jt400.jar`).  The native driver is supported, but a license file is required.

And system differences:

 * Each database adapter must implement two queries: one to return column names, and one to return procedure parameters.  The system catalog varies from platform to platform:

   * On DB2 for LUW, `SYSCAT.COLUMNS` and `SYSCAT.SYSROUTINEPARMS` are queried.
   * On DB2 for System i, `QSYS2.SYSCOLUMNS` and `QSYS2.SYSPARMS` are queried.
   * On DB2 for System z, `SYSIBM.SYSCOLUMNS` and `SYSIBM.SYSPARMS` are queried.
   * On DB2 for LUW, parameter direction is stored as a letter code.  On System i, it is stored as a word.

### Which DB2 adapter should I use?

 *  Use DB2Test if your DB2 instance under test is on Windows, Linux, Unix, or System z.
     * This adapter uses IBM's "native" JDBC driver: `db2jcc.jar`.
 *  Use DB2iSeriesTest if your DB2 instance is on an iSeries (formerly AS/400).
     * This adapter uses JTOpen's Toolbox driver for IBM i: `jt400.jar`
     * The Toolbox Driver is free, open source, and available here: <http://jt400.sourceforge.net/>

#### Setup Script to run Acceptance Tests

Two of the functions required for the Acceptance Tests need their syntax modified for DB2. DB2 does not support `AUTO_INCREMENT` or `CONCAT` taking three arguments.  They are:

    CREATE TABLE USERS(NAME VARCHAR(50) UNIQUE,
                       USERNAME VARCHAR(50),
                       USERID INT NOT NULL
                                  GENERATED ALWAYS AS IDENTITY
                                  (START WITH 1, INCREMENT BY 1))

and

    CREATE FUNCTION ConcatenateF
      (firststring varchar(100),
       secondstring varchar(100))
    RETURNS varchar(200)
      RETURN CONCAT(firststring, concat(' ', secondstring))

### DB2 for System i Considerations

#### Choosing a driver

Which JDBC driver should I use?

  * Currently `DB2Environment` uses the native driver, and `DB2iEnvironment` uses the Toolbox driver.  Using the native driver on System i is possible, but at present requires a recompile of `DB2iEnvironment`.  Consider the following:
  * The Native driver runs only on the IBM i JVM, but performs better than the Toolbox driver when the data is on the same machine. The Toolbox driver runs on any JVM (including the JVM shipped with IBM i). The current general advice is this: If your program is only intended to run on the IBM i JVM and the data is on the same machine, use the Native driver. If your program is intended to run on other JVMs or the Java program is on one IBM i system and the data is on a different IBM i system, use the Toolbox driver.
  * [Source](http://www-03.ibm.com/systems/i/software/toolbox/faqjdbc.html#faqA1)

#### Using the default database

If the default database (often the tables of interest to our testing) must be journalled, acceptance tests with INSERT and UPDATE operations may fail with:


    java.sql.SQLException: [SQL7008] TEST_DBFIT in TESTLIB not valid for operation. Cause . . . . . :   
    The reason code is 3.  Reason codes are: 1 -- TEST_DBFIT has no members. 2 -- TEST_DBFIT has been saved with storage free. 3 -- TEST_DBFIT not journaled, no authority to the journal, or the journal state is *STANDBY.  
    Files with an RI constraint action of CASCADE, SET NULL, or SET DEFAULT must be journaled to the same journal.


There are two ways around this:

 * Create a new database schema, separate from the default database, and run the acceptance tests against it.  DB2 will automatically create a journal receiver for the new database.
 * Add `transaction isolation=none` to the connection string.

### Links

* [Schemas and journals](http://www.ibm.com/developerworks/data/library/techarticle/0305milligan/0305milligan.html)
* [Using the native driver on iSeries](http://publib.boulder.ibm.com/infocenter/db2luw/v9r5/index.jsp?topic=%2Fcom.ibm.db2.luw.apdv.java.doc%2Fsrc%2Ftpc%2Fimjcc_t0010264.html)
* [Differences between Toolbox driver and Native driver on iSeries](http://publib.boulder.ibm.com/infocenter/radhelp/v7r0m0/index.jsp?topic=/com.ibm.datatools.connection.ui.doc/topics/rdbconn_db2udb_iseries.html)
* [Toolbox FAQ](http://www-03.ibm.com/systems/i/software/toolbox/faqjdbc.html)
* [More on IBM JDBC drivers](http://publib.boulder.ibm.com/infocenter/db2luw/v8/index.jsp?topic=/com.ibm.db2.udb.dc.doc/dc/r_jdbcdrivers.htm)