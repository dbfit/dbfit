## Transaction Control

By default, each individual test (FitNesse page) in flow mode is executed in a transaction that is automatically rolled back after the test. In standalone mode, you are responsible for overall transaction control.

If in flow mode, you can use the Commit and Rollback commands to control the transactions manually, but remember that a final rollback will be added at the end of the test. These commands have no additional arguments.

In standalone mode, you will probably control transactions from outside DbFit. Utility commands to commit and rollback are still provided, if you need them. For example, use this table to rollback:

    |Rollback|

Or in order to commit:

    |Commit|

For standaone mode only, an alternative syntax is also still supported:

    !|DatabaseEnvironment|
    |Rollback|

### Autocommit mode

By default DbFit is using `autocommit=false` mode - which means that the database statements are executed in one transaction. In some special occasions, you may need to change that. It's possible to configure database connection's autocommit mode via:

    |set option|autocommit|true|

Possible values are:

 * `true` - automatically commit after each statement
 * `false` (this is the default) - transaction is terminated on explicit commit or rollback (for flow mode such is performed at the end of the transaction).
 * `auto` - driver default (DbFit doesn't explicitly set autocommt mode, the JDBC driver decides)
