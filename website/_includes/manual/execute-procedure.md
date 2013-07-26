## Execute Procedure

`Execute procedure` executes a stored procedure or function for each row of data table, binding input/output parameters to columns of the data table, eg:

    !|Execute Procedure|MyStoredProcedure|
    |param1|param2|sum?                  |
    |2     |2     |4                     |
    |5     |6     |11                    |

### Syntax

1. **First row** *(mandatory)*

    * first cell *(mandatory)*: Execute procedure
    * second cell *(mandatory)*: the procedure or function name

2. **Second row** *(optional, if the procedure has no parameters)*

     * parameter names (output parameters followed by a question mark)
     * parameter order or case is not important
     * you can even insert blanks and split names into several words to make the test page more readable. Blanks are removed by DbFit to get the parameter name (eg `second string` => `secondstring`):

            !|Execute Procedure|ConcatenateStrings   |
            |first string|second string|concatenated?|
            |Hello       |World        |Hello World  |
            |Ford        |Prefect      |Ford Prefect |

    * to use IN/OUT parameters, you'll need to specify the parameter twice. Once without the question mark, when it is used as the input; and one with the question mark when it is used as output:

            |execute procedure|Multiply|
            |factor|val|val?           |
            |5     |10 |50             |

3. **Subsequent rows** *(optional, if the procedure has no parameters)*

    * you can store any output value into a parameter with the `>>` syntax or send current parameter values to procedure using `<<` syntax:

            !|Execute Procedure|ConcatenateStrings   |
            |first string|second string|concatenated?|
            |Hello       |World        |Hello World  |
            |Ford        |Prefect      |Ford Prefect |

    * if the procedure has **no** output parameters, then the `Execute Procedure` command has no effect on the outcome of the test — unless an error occurs during processing.
    * if the procedure has output parameters, then those values are compared to expectations specified in the FitNesse table, and are used to determine the outcome of the test.

### Calling procedures without parameters

For the case where no parameters are passed to function/procedure, `Execute Procedure` can be specified with just one row (without a row for column header names).

    !|Execute Procedure|MakeUser|

### Calling functions

If a function is getting called, then a column containing just the question mark is used for function results.

    !3 Stored functions are treated like procs - just put ? in the result column header

    !|Execute Procedure|ConcatenateF        |
    |first string|second string|?           |
    |Hello       |World        |Hello World |
    |Ford        |Prefect      |Ford Prefect|

    !3 ? does not have to appear on the end (although it is a good practice to put it there)

    !|Execute Procedure|ConcatenateF        |
    |second string|?           |first string|
    |World        |Hello World |Hello       |
    |Prefect      |Ford Prefect|Ford        |

### Expecting exceptions

If you want to test a boundary condition that should cause an exception, then use `Execute procedure expect exception` variant of the `Execute procedure` command, eg:

    !|execute procedure expect exception|createuser|
    |new name   |new username                      |
    |arthur dent|adent                             |

In flow mode, this command can also be used to check for exceptions during processing. Normally, the test would fail if a database exception occurs. You can even specify an optional exception code as the third argument. If no exception code is specified, then the test will pass if any error occurs for each data row. If the third argument is specified, then the actual error code is also taken into consideration for failing the test.

    !3 create a user so that subsequent inserts would fail

    !|execute procedure|createuser|
    |new name   |new username     |
    |arthur dent|adent            |

    !3 check for any error

    !|execute procedure expect exception|createuser|
    |new name   |new username                      |
    |arthur dent|adent                             |

    !3 check for a specific error code

    !|execute procedure expect exception|createuser|1062|
    |new name   |new username                           |
    |arthur dent|adent                                  |

For detailed exception code verifications to work with SQL Server, user message must be registered for that particular error code, or SQL Server throws a generic error code outside the database. Here is how you can declare your error code:

    sp_addmessage @msgnum = 53120, @severity=1, @msgtext = 'test user defined error msg' 

 `Execute procedure expect exception` variant is not directly available as a separate table in standalone mode. If you need this functionality in standalone mode, then extend the `ExecuteProcedure` fixture and call the appropriate constructor. That class has several constructors for exceptions and error codes.
