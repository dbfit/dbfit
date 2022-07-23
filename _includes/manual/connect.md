
## Connect

Use `Connect` to initialise the database connection. Alternatively, use [`ConnectUsingFile`](#connect-using-file) to initialise the connection using properties from a file.

The `Connect` table can be used in two ways:

### 1. Specifying server, username, password, database name

Pass the server (optionally followed by the instance name), username, password, and the database name as arguments.

For example, to connect to a locally installed version of SqlServer 2005 Express:

    !|Connect|LAPTOP\SQLEXPRESS|FitNesseUser|Password|TestDB|

 *  If you are connecting to a default database, you can omit the fourth parameter.
 *  For the .NET version, you can do this for Oracle, because the second argument is the TNS Name.
 *  The Java version of DbFit uses the Thin driver for Oracle, and expects the second argument to be the host name (with an optional port) and the fourth argument to be the service identifier.

Here is an Oracle example:

    !|Connect|localhost:1521|FitNesseUser|Password|XE|

### 2. Specifying the full connection string

If you want to use non-standard connection properties, or initialise your connection differently, call `Connect` with a single argument - the full ADO.NET or JDBC connection string. Here is an example:

    |Connect|jdbc:sqlserver://myhost\myinstance;user=myuser;password=mypassword;databaseName=mydbname|

You can use this feature, for example, to utilise Windows integrated authentication or to use the OCI driver for Oracle under Java.

### 3. Additional references

For additional information about database connection settings, please refer to:

* the [Database-specific Information](database-specific-information.html) section
* the database vendor's official site

<div class="alert alert-warning alert-block">
If you need to open a second connection it's advised to close the current one first using <code>|Close|</code> command.
</div>

