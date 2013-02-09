---
layout: page
title: Reference
nav_bar_name: docs
show_comments: true
---
## Working with parameters

DbFit enables you to use Fixture symbols as global variables during test execution, to store or read intermediate results. The .NET syntax to access symbols (`>>parameter` to store a value and `<<parameter` to read the value) is supported in both .NET and Java versions. In addition, you can uset he `Set Parameter` command to explicitly seta parameter value to a string.

<pre>
|Set parameter|username|arthur|
</pre>

DbFit is type sensitive, which means that comparing strings to numbers, even if both have the value 11, will fail the test. Most databases will allow you to pass strings into numeric arguments, but if you get an error that a value is different than expected and it looks the same, it is most likely due to a wrong type conversion. Keep that in mind when using `Set parameter`. A good practice to avoid type problems is to read out parameter values from a query. This will be explained in detail soon.

You can also use the keyword `NULL` to set a parameter value to `NULL`.

## Query

`Query` is similar to traditional FIT `RowFixture`, but uses SQL Query results. You should specify query as the first fixture parameter, after the `Query` command. The second table row contains column names, and all subsequent rows contain data for the expected results. You do not have to list all columns in the result set — just the ones that you are interested in testing.

<pre>
!|Query| select 'test' as x|
|x|
|test|
</pre>

### Ordering and row matching

`Query` ignores row order by default. In flow mode, the `Ordered Query` command provides order checking.

Partial key matching is supported, like in RowFixture: columns with a question mark in their name are not used to match rows, just for value comparisons. You can use this to get better error reports in case of failed tests. It is a good practice to put a question mark after all column names that are not part of the primary key.

Rows in the actual result set and FitNesse table are matched from top to bottom, looking for equal values in all cells that are not marked with a ques- tion mark. If there are no key columns, then the first row will be taken as a match (which effectively acts as the `Ordered Query`). All non-key columns are used for value comparisons, not for deciding whether or not a row exists in the result set.

`Query` will report any rows that exist in the actual result set and not in the FitNesse table (those will be marked as *surplus*), rows that exist in the FitNesse table but not in the actual result set (marked as *missing*). All matched rows are then checked for values in columns, and any differences will be reported in individual cells. You can use a special `fail [expected value]` syntax to invert the test, making it fail if a certain value appears in the row:

<pre>
This will fail because the order is wrong
|Ordered Query|SELECT n FROM ( SELECT 1 as n union select 2 union select 3 ) x |
|n|
|fail[2]|
|fail[1]|
|3|

This will pass because the order is correct
|Ordered Query|SELECT n FROM ( SELECT 1 as n union select 2 union select 3 ) x|
|n|
|1|
|2|
|3|
</pre>

### Using parameters

You can use query parameters (DB-specific syntax is supported, eg. `@paramname` for SQLServer and MySQL, and `:paramname` for Oracle). Corresponding fixture symbol values are automatically used for named query parameters.

<pre>
|Set Parameter|depth|3|

|Query|SELECT n FROM ( SELECT 1 as n union select 2 union select 3 union select
 4) x where n&lt;@depth |
|n|
|2|
|1|
</pre>

You can store elements of the result set into parameters — to re-use them later in other queries and stored procedures. Use `>>parameter` to store a cell value into a parameter. You can also use `<<parameter` to read a cell value from a parameter (for comparisons, for example).

If you use the query just to read out stuff into parameters, then make sure to mark the columns with the question mark to avoid row matching. There will be nothing to match the rows with in this case, so a proper comparison would fail.

<pre>
!|query|select now() as currd|
|currd?|
|>>tsevt|
</pre>

To test for an empty query, you still need to specify the second row (result set structure), but don't supply any data rows.

### Avoiding parameter mapping

If you want to prevent DbFit from mapping parameters to bind variables (eg to execute a stored procedure definition that contains the @ symbol in Sql Server), disable `bind symbols` option before running the query.

<pre>
|set option|bind symbols|false|

|execute| insert into users (name, username) values ('@hey','uuu')|

|query|select * from users|
|name|username|
|@hey|uuu|
</pre>

Remember to re-enable the option after the query is executed. You can use the same trick with the Execute command.

### Multi-line queries and special characters

You can use multi-line queries by enclosing them into `!-` and `-!`. This will also prevent any special character formatting. This trick can also be used with Oracle to prevent the concatenation operator `||` from being treated as a FitNesse cell boundary.

### Working with padded chars

Some databases treat `CHAR` type as fixed length and fill content up to the specified length with spaces. FitNesse strips trailing spaces by default from cell contents, which makes it hard to compare `CHAR` types. DbFit provides a workaround for this, that must be enabled manually since it modifies stan- dard string parsing. To enable this option, include the following table in your tests:

<pre>
|set option|fixed length string parsing|true|
</pre>

Afterthat,youcanenclosestringsintosingle-quotes(`'my string'`)andput trailing spaces before the closing quote. This allows you to ensure that the correct length of the string is used for comparisons. Here is an example (this example is for SQL Server, since MySql strips trailing spaces):

<pre>
!3 use fixed string length parsing to test blank-padded chars

|Execute|Create table datatypetest (s1 char(10), s2 nchar(10))|

|set option|fixed length string parsing|true|

|insert|datatypetest|
|s1|s2|
|testch|testnch|

direct comparison will fail

|query|select * from datatypetest|
|s1?|s2?|
|fail[testch]|fail[testnch]|

use single quotes to pad to appropriate length

|query|select * from datatypetest|
|s1?|s2?|
|'testch    '|'testnch   '|
</pre>

## Insert

`Insert` is the database equivalent of FitLibrary `SetupFixture` — it builds an insert command from the parameters in a data table (and executes the insert once for each row of the table). The view or table name is given as the first fixture parameter. The second row contains column names, and all subsequent rows contain data to be inserted.

<pre>
|Execute|Create table Test_DBFit(name varchar(50), luckyNumber int)|

|Insert|Test_DBFit|
|name|luckyNumber|
|pera|1|
|nuja|2|
|nnn|3|

|Query|Select * from Test_DBFit|
|name|lucky Number|
|pera|1|
|nuja|2|
|nnn|3|

|Execute|Drop table Test_DBFit|
</pre>

### Storing auto-generated values

Columns with a question mark are used as outputs. When an output column is used, it will contain the value of the column in the new record. This is especially handy for retrieving an auto-generated primary key. For Oracle, this works regardless of whether the column was actually the ID or some- thing else populated with a trigger. For MySQL and SQL Server, only single- column actual primary keys can be returned. The only thing that makes sense to do at this point is to store values of the output cells into variables.

<pre>
!3 Use ? to mark columns that should return values

!|Insert|users|
|username|name|userid?|
|pera|Petar Detlic|>>pera|
|Mika|Mitar Miric|>>mika|
|Zeka|Dusko Dugousko|>>zeka|
|DevNull|null|>>nll|

!3 Confirm that IDs are the same as in the database

!|Ordered Query|Select * from users|
|username|name|userid|
|pera|Petar Detlic|&lt;&lt;pera|
|Mika|Mitar Miric|&lt;&lt;mika|
|Zeka|Dusko Dugousko|&lt;&lt;zeka|
|DevNull|null|&lt;&lt;nll|

!3 Stored values can be used in queries directly

|Query|Select * from users where userid=@zeka|
|username|name|userid|
|Zeka|Dusko Dugousko|&lt;&lt;zeka|
</pre>

When the test runs, you will see actual values being stored into variables.

## Update

Update allows you to quickly script data updates. It builds the update command from the parameters in a data table and executes the update once for each row of the table. Columns ending with `=` are used to update records (cell specifies new data value). Columns without `=` on the end are used to select rows (cell specifies expected column value for the select part of update command). The view or table name is given as the first fixture parameter. The second row contains column names, and all subsequent rows contain data to be updated or queried. This example updates the `username` column where the name matches `arthur dent`.

<pre>
|insert|users|
|name|username|
|arthur dent|adent|
|ford prefect|fpref|
|zaphod beeblebrox|zaphod|

|update|users|
|username=|name|
|adent2|arthur dent|

|query|select * from users|
|name|username|
|arthur dent|adent2|
</pre>

You can use multiple columns for both updating and selecting, and even use the same column for both operations. You can also use parameters — eg. `<<paramname` — in any cell.

## Execute Procedure

`Execute Procedure` is the equivalent of `ColumnFixture`. It executes a stored procedure or function for each row of data table, binding input/output parameters to columns. The procedure name should be given as the first fixture parameter. The second row should contain parameter names (output parameters followed by a question mark). All subsequent rows are data rows, containing input parameter values and expected values of output parameters. Parameter order or case is not important, you can even insert blanks and split names into several words to make the test page more readable.

<pre>
!3 execute procedure allows multiple parameters, with blanks in names

!|Execute Procedure|ConcatenateStrings|
|first string|second string|concatenated?|
|Hello|World|Hello World|
|Ford|Prefect|Ford Prefect|
</pre>

You can store any output value into a parameter with the `>>` syntax or send current parameter values to procedure using `<<` syntax.

To use IN/OUT parameters, you'll need to specify the parameter twice. Once without the question mark, when it is used as the input; and one with the question mark when it is used as output.

<pre>
!3 IN/OUT params need to be specified twice

|execute procedure|Multiply|
|factor|val|val?|
|5|10|50|
</pre>

If the procedure has no output parameters, then the `Execute Procedure` command has no effect on the outcome of the test — unless an error occurs during processing. If the procedure has output parameters, then those values are compared to expectations specified in the FitNesse table, and are used to determine the outcome of the test.

For the case where no parameters are passed to function/procedure, `Execute Procedure` can be specified with just one row (without a row for column header names).

<pre>
!3 If there are no parameters, Execute Procedure needs just one row

!|Execute Procedure|MakeUser|
|query|select * from users|
|name|username|
|user1|fromproc|
</pre>

### Calling Functions

If a function is getting called, then a column containing just the question mark is used for function results.

<pre>
!3 Stored functions are treated like procs - just put ? in the result column
 header

!|Execute Procedure|ConcatenateF|
|first string|second string|?|
|Hello|World|Hello World|
|Ford|Prefect|Ford Prefect|

!3 ? does not have to appear on the end (although it is a good practice to put
 it there)

!|Execute Procedure|ConcatenateF|
|second string|?|first string|
|World|Hello World|Hello|
|Prefect|Ford Prefect|Ford|
</pre>

### Expecting exceptions

In flow mode, this command can also be used to check for exceptions during processing. Normally, the test would fail if a database exception occurs. However, if you want to test a boundary condition that should cause an exception, then use `Execute procedure expect exception` variant of the `Execute procedure` command. You can even specify an optional exception code as the third argument. If no exception code is specified, then the test will pass if any error occurs for each data row. If the third argument is specified, then the actual error code is also taken into consideration for failing the test.

<pre>
!3 create a user so that subsequent inserts would fail

!|execute procedure|createuser|
|new name|new username|
|arthur dent|adent|

!3 check for any error

!|execute procedure expect exception|createuser|
|new name|new username|
|arthur dent|adent|

!3 check for a specific error code

!|execute procedure expect exception|createuser|1062|
|new name|new username|
|arthur dent|adent|
</pre>

For detailed exception code verifications to work with SQL Server, user message must be registered for that particular error code, or SQL Server throws a generic error code outside the database. Here is how you can declare your error code:

<pre>
￼sp_addmessage @msgnum = 53120, @severity=1, @msgtext = 'test user defined error
 msg'
 </pre>

 `Execute procedure expect exception` variant is not directly available as a separate table in standalone mode. If you need this functionality in standalone mode, then extend the `ExecuteProcedure` fixture and call the appropriate constructor. That class has several constructors for exceptions and error codes.

 ## Execute

 `Execute` executes any SQL statement. The statement is specified as the first fixture parameter. There are no additional rows required for this command.

 You can use query parameters in the DB-specific syntax (eg. `@paramname` for SQLServer and MySQL, and `:paramname` for Oracle). Currently, all parameters are used as inputs, and there is no option to persist any statement outputs.

 <pre>
!3 to execute statements, use the 'execute' command

|Execute|Create table Test_DBFit(name varchar(50), luckyNumber int)|

|Execute|Insert into Test_DBFit values ('Obi Wan',80)|
 
|Set parameter|name|Darth Maul|

|Execute|Insert into Test_DBFit values (@name,10)|

|Query|Select * from Test_DBFit|
|Name|Lucky Number|
|Darth Maul|10|
|Obi Wan|80|

|Execute|Drop table Test_DBFit|
 </pre>

## Inspect

`Inspect` is a utility fixture class used to quickly extract meta-data information from the database, and print it out in a form which can be easily converted into a test. It can work in three modes: `Query`, `Table` or `Procedure`. In the `Query` mode, it expects a full query as argument (bound variables are supported), and prints out both the result structure and result data. In the `Table` mode, it expects a table or view name as an argument and prints out the table or view column names (without actual data, just the structure). In `Procedure` mode, it expects a procedure name as an argument and prints out the procedure parameter names. These tables can be easily converted into `Query`, `Execute Procedure`, `Insert` or `Update` tables.

In flow mode, these three inspections are available as individual commands `Inspect query`, `Inspect table` and `Inspect procedure`. In standalone mode, you can extend the `Inspect fixture` and set the appropriate mode manually while calling the constructor.

<pre>
!3 Inspect Procedure prints procedure arguments to be used for Execute procedure

!|Inspect Procedure|ConcatenateStrings|

!3 Inspect Table prints table/view columns to be used for Insert/Update/Query
 procedure

!|Inspect Table|users|

!3 Inspect query prints columns and data

|Insert|users|
|name|username|
|david haselhoff|dhoff|
|arthur dent|adent|

!|Inspect query|select * from users|
</pre>

When the test is executed, FitNesse will append meta-data and results to the test tables in gray colour. To convert the results into a new test, select the entire table in the browser, directly from the rendered results page (not from the HTML source or wiki source), and copy it. Internet Explorer allows you to get just a few rows at a time, while in some versions of Firefox you have to select the entire table in order to copy it properly. Edit the test page, delete the old table and paste the contents of the clipboard into the page editor. You should see the results table with column values separated by tabs. Click the Spreadsheet to FitNesse button below the editor text box. This turns the tab-separated results table into a FitNesse test table, converting the tabs into pipes to separate cells and even putting the exclamation mark before the first row automatically.

## Store Query

`Store Query` reads out query results and stores them into a Fixture symbol for later use. Specify the full query as the first argument and the symbol name as the second argument (without `>>`). You can then use this stored result set as a parameter of the `Query` command later:

<pre>
!|Store Query|select n from ( select 1 as n union select 2 union select 3) x|
firsttable|

!|query|&lt;&lt;firsttable|
|n|
|1|
|2|
|3|
</pre>

You can also directly compare two stored queries and check for differences.

## Compare Stored Queries

`Compare Stored Queries` compares two previously stored query results. Specify symbol names as the first and second argument (without `<<`). The query structure must be listed in the second row. (Use Inspect Query to build it quickly if you do not want to type it.) Column structure is specified so that some columns can be ignored during comparison (just don’t list them), and for the partial row-key mapping to work. Put a question mark after the column names that do not belong to the primary key to make the compar- isons better. The comparison will print out all matching rows in green, and list rows that are in just one query with red (and fail the test if such rows exist). If some rows are matched partially, just by primary key, differences in individual value cells will also be shown and the test will fail.

<pre>
|execute|create table testtbl (n int, name varchar(100))|

!|insert|testtbl|
|n|name|
|1|NAME1|
|3|NAME3|
|2|NAME2|

|Store Query|select * from testtbl|fromtable|

|Store Query|select n, concat('NAME',n) as name from ( select 1 as n union
 select 3 union select 2) x|fromdual|

|compare stored queries|fromtable|fromdual|
|name|n?|

|execute|drop table testtbl|
</pre>

## Transaction control

By default, each individual test (FitNesse page) in flow mode is executed in a transaction that is automatically rolled back after the test. In standalone mode, you are responsible for overall transaction control.

If in flow mode, you can use the Commit and Rollback commands to control the transactions manually, but remember that a final rollback will be added at the end of the test. These commands have no additional arguments.

In standalone mode, you will probably control transactions from outside DbFit. Utility commands to commit and rollback are still provided, if you need them, as part of the DatabaseEnvironment fixture. For example, use this table to rollback:

<pre>
!|DatabaseEnvironment|
|Rollback|
</pre>