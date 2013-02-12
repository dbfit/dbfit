## Transaction Control

By default, each individual test (FitNesse page) in flow mode is executed in a transaction that is automatically rolled back after the test. In standalone mode, you are responsible for overall transaction control.

If in flow mode, you can use the Commit and Rollback commands to control the transactions manually, but remember that a final rollback will be added at the end of the test. These commands have no additional arguments.

In standalone mode, you will probably control transactions from outside DbFit. Utility commands to commit and rollback are still provided, if you need them, as part of the DatabaseEnvironment fixture. For example, use this table to rollback:

    !|DatabaseEnvironment|
    |Rollback|
