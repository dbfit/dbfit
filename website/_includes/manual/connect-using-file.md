## Connect Using File

You can also store connection properties in a file, then initialise the connection using the `ConnectUsingFile` table. This allows you to hide actual database usernames and passwords from FitNesse users, should you need to do so.

`ConnectUsingFile` has only one argument - the path of the file on the server, either absolute or relative to the folder from which you started FitNesse (the one containing `startFitnesse`). The connection properties file is a plain text file, containing key/value pairs separarted by the equals symbol (`=`). Lines starting with a hash (`#`) are ignored. Use the following keys (they care case-sensitive):

 1. `service` - service name, eg `LAPTOP\SQLEXPRESS`. 
 2. `username` - username to connect to the database.
 3. `password` - password to connect to the database.
 4. `database` - optional fourth argument, allowing you to choose the active database.
 5. `connection-string` - alternative to the four previous parameters, this allows you to specify the full connection string. This parameter should not be mixed with any of the four other keys. Use either the full string or specify individual properties.

Here is an example:

    # DBFit connection properties file
    #
    #1) Either specify full connection string
    #connection-string=
    #
    #2) OR specify service, username and password as separate properties
    service=localhost
    username=root
    password=
    #optionally specify a database name
    database=dbfit
