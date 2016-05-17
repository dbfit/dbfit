---
layout: page
title: Database-specific Information
nav_bar_name: docs
show_comments: true
---

*   [DB2](#db2)
*   [MS SQL Server](#microsoft-sql-server)
*   [Teradata](#teradata)
*   [Netezza](#netezza)
*   [Informx](#informix)

---

## DB2

_Contributed by [Mike Patrick](https://github.com/mikepatrick)_

### Why are there multiple DB2 adapters?

There are essentially three flavors of DB2:

 * DB2 for LUW (Linux, Unix, Windows).
 * DB2 for System i.
 * DB2 for System z.

Each flavor comes with its own peculiarities.  There are driver differences:

 * DB2 for LUW and DB2 for System Z use IBM's native JDBC driver (`db2jcc4.jar`).  On System z, a license file is required.
 * DB2 for System i most often uses IBM's Java Toolbox Driver (`jt400.jar`).  The native driver is supported, but a license file is required.

And system differences:

 * Each database adapter must implement two queries: one to return column names, and one to return procedure parameters.  The system catalog varies from platform to platform:

   * On DB2 for LUW, `SYSCAT.COLUMNS` and `SYSCAT.SYSROUTINEPARMS` are queried.
   * On DB2 for System i, `QSYS2.SYSCOLUMNS` and `QSYS2.SYSPARMS` are queried.
   * On DB2 for System z, `SYSIBM.SYSCOLUMNS` and `SYSIBM.SYSPARMS` are queried.
   * On DB2 for LUW, parameter direction is stored as a letter code.  On System i, it is stored as a word.

### Which DB2 adapter should I use?

 *  Use DB2Test if your DB2 instance under test is on Windows, Linux, Unix, or System z.
     * This adapter uses IBM's "native" JDBC driver: `db2jcc4.jar`. It can be downloaded from [IBM's web site](http://www.ibm.com/support/docview.wss?rs=4020&uid=swg21385217)
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

----

## Microsoft SQL Server

### Deploying the JDBC Driver

1. [Download MS SQL Server JDBC driver from their site](http://www.microsoft.com/en-us/download/details.aspx?id=11774) *(It is not open source and cannot be distributed with DbFit.)*
2. Deploy `sqljdbc4.jar` in DbFit's `lib` folder - the same folder as dbfit-XXX.jar.
3. Deploy auth\x86\sqljdbc_auth.dll in `%programfiles(x86)%\Java\jre7\bin` folder to allow `integrated windows authentication`

### Database Engine configuration 

* Make sure the engine is configured to allow TCP/IP connections. Launch SQL Server Configuration. SQL Server Network Configuration > SQL Server > Right click TCP/IP and choose Enable. 
* Make sure you restart your engine after you enable TCP/IP connections.
* Make sure you allow mixed-mode authentication so you can use Logins created to use SQL Authentication.

#### SQL Express Specific 

* Enable and start the SQL Browser Service. Change the service state from Disabled to Auto or Manual, then Start the service. The driver will indicate that it cannot communicate to UDP port 1434 if the SQL Browser Service is not started.

### Network and firewall settings

* If connecting over network each database should be configured to accept TCP/IP connections (including allowance in firewall if any).
* Connection to named instance requires MS SQL Server Browser service to be up and running and allowed by firewalls.

### Connection string to MS SQL Server database

* Check DbFit reference documentation: [ConnectUsingFile](/dbfit/docs/reference.html#connect-using-file) and [Connect](/dbfit/docs/reference.html#connect).
* Refer to the [MS SQL Server docs](http://technet.microsoft.com/en-us/library/ms378428.aspx) for details about JDBC url syntax.


#### Examples

* Using host and port number

`!|Connect|myhost:1433|myuser|mypassword|mydbname|`

* Default port is 1433

`!|Connect|myhost|myuser|mypassword|mydbname|`

* Using instance name with backslash prefix

`!|Connect|myhost\myinstance|myuser|mypassword|mydbname|`

* Using instance name property

`!|Connect|myhost;instanceName=myinstance|myuser|mypassword|mydbname|`

* Using raw JDBC url

`!|Connect|jdbc:sqlserver://myhost\myinstance;user=myuser;password=mypassword;databaseName=mydbname|`

* Using integrated security

`!|Connect|jdbc:sqlserver://myhost;integratedSecurity=true|`

----

## Teradata

### Deploying the JDBC Driver

1. [Download the Teradata JDBC driver from their site](http://downloads.teradata.com/download/connectivity/jdbc-driver) *(It is not open source and cannot be distributed with DbFit.)*
2. Deploy `terajdbc4.jar` and `tdgssconfig.jar` in DbFit's `lib` folder - the same folder as dbfit-XXX.jar.

### Connection string to a Teradata database

* Check DbFit reference documentation: [ConnectUsingFile](/dbfit/docs/reference.html#connect-using-file) and [Connect](/dbfit/docs/reference.html#connect).
* Refer to the [Teradata JDBC Driver Reference](http://developer.teradata.com/connectivity/reference/jdbc-driver) for details about JDBC url syntax.


#### Examples

* Using Teradata server host name, user name, password and default database

`!|Connect|myhost|myuser|mypassword|mydbname|`

* Using Teradata server host name, user name, password and no default database

`!|Connect|myhost|myuser|mypassword|`

* Using LDAP as the authentication source (note it is not currently possible to specify a default database name and LDAP as an authentication source with this method)

`!|Connect|myhost/LOGMECH=LDAP|myuser|mypassword|`

* Using Teradata as the transaction mode.authentication source (note it is not currently possible to specify a default database name and a transaction mode wiht thi method)

`!|Connect|myhost/TMODE=TERA|myuser|mypassword|`

* Using ANSI as the transaction mode.authentication source (note it is not currently possible to specify a default database name and LDAP as an authentication source with this method)

`!|Connect|myhost/TMODE=ANSI|myuser|mypassword|`

* Using raw JDBC url (note that a default database, a logon authentication source and a transaction mode can all be specified with this method)

`!|Connect|jdbc:teradata://myhost/user=myuser,password=mypassword,database=mydbname,tmode=TERA,logmech=td2|`

----

## Netezza

### Deploying the JDBC Driver

1. [Download the Netezza JDBC driver from their site](https://www14.software.ibm.com/webapp/iwm/web/reg/pick.do?source=swg-im-ibmndn&lang=en_US) *(It is not open source and cannot be distributed with DbFit.)*
2. Deploy `nzjdbc.jar` in DbFit's `lib` folder - the same folder as dbfit-XXX.jar.

### Connection string to a Netezza database

* Check DbFit reference documentation: [ConnectUsingFile](/dbfit/docs/reference.html#connect-using-file) and [Connect](/dbfit/docs/reference.html#connect).
* URL format is	jdbc:netezza://<HOST>:<PORT>/<DATABASE_NAME>

#### Examples

* Using Netezza Emulator default IP address and port, user name, password and test database

`!|Connect|192.168.85.128:5480|admin|password|NETEZZATEST|`

----

## Informix

1. [Download the Informix driver from the IBM site] (http://www-03.ibm.com/software/products/en/infjdbc).
2. Deploy `ifxjdbc.<major>-<minor>-<tag>.jar` (e.g. `ifxjdbc-4.10.JC4W1.jar`) in DbFit's `lib` folder - the same folder as dbfit-XXX.jar.

### Connection string to an Informix database

* Check DbFit reference documentation: [ConnectUsingFile](/dbfit/docs/reference.html#connect-using-file) and [Connect](/dbfit/docs/reference.html#connect).
* Refer to the [Informix JDBC Driver Reference](http://www-01.ibm.com/support/knowledgecenter/SSGU8G_12.1.0/com.ibm.jdbc_pg.doc/jdbc.htm) for details about JDBC url syntax.

#### Examples

* Using the Informix server host name, user name, password and database name

`!|Connect|<myHost>:<myTcpPortNumber>|<myUserName>|<myPassword>|<myDbName>|`

* Using a raw JDBC URL

`!|Connect|jdbc:informix-sqli://<myHost>:<myTcpPortNumber>/<myDbName>:INFORMIXSERVER=<myServer>;user=<myUserName>;password=<myPassword>|`

