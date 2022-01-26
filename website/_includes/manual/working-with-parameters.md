## Working with parameters

DbFit enables you to use Fixture symbols as global variables during test execution, to store or read intermediate results. The .NET syntax to access symbols (`>>parameter` to store a value and `<<parameter` to read the value) is supported in both .NET and Java versions. To suppress the output of the value you may use `>>>parameter` and `<<<parameter`. In addition, you can use the `Set Parameter` command to explicitly set a parameter value to a string.

    |Set parameter|username|arthur|

DbFit is type sensitive, which means that comparing strings to numbers, even if both have the value 11, will fail the test. Most databases will allow you to pass strings into numeric arguments, but if you get an error that a value is different than expected and it looks the same, it is most likely due to a wrong type conversion. Keep that in mind when using `Set parameter`. A good practice to avoid type problems is to read out parameter values from a query. This will be explained in detail soon. Another way to solve that problem is to specify a custom parser:

    |Set parameter|partscount|7|java.lang.Integer|

The last parameter, `java.lang.Integer` in the above example, is expected to be class name with a `static` method `Object valueOf(String)` or `Object parse(String)`. DbFit will use it to convert the given text to a Java object, and that object will be set as the parameter value.

You can also use the keyword `NULL` to set a parameter value to `NULL`.
