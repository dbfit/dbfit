## Compare Stored Queries

`Compare Stored Queries` compares two previously stored query results. Specify symbol names as the first and second argument (without `<<`). The query structure must be listed in the second row. (Use Inspect Query to build it quickly if you do not want to type it.) Column structure is specified so that some columns can be ignored during comparison (just don’t list them), and for the partial row-key mapping to work. Put a question mark after the column names that do not belong to the primary key to make the comparisons better. The comparison will print out all matching rows in green, and list rows that are in just one query with red (and fail the test if such rows exist). If some rows are matched partially, just by primary key, differences in individual value cells will also be shown and the test will fail.

    |execute|create table testtbl (n int, name varchar(100))|

    !|insert|testtbl|
    |n      |name   |
    |1      |NAME1  |
    |3      |NAME3  |
    |2      |NAME2  |

    |Store Query|select * from testtbl|fromtable|

    |Store Query|!-select n, concat('NAME',n) as name from ( select 1 as n union
     select 3 union select 2) x-!|fromdual|

    |compare stored queries|fromtable|fromdual|
    |name                  |n?                |

    |execute|drop table testtbl|

### Comparing large data sets

A good test should be focused and fast to execute. That helps to get a quick and precise feedback which leads the root cause of the test failure. That usually implies using very small data sets (1-3 rows) in `DbFit` tests. Nevertheless, in some special cases it may be desired to run `DbFit` on top of relatively large number of rows.

#### Suppressing successfully matched rows

When a large number of rows is being compared, match failures can be hard to see amongst the matched rows. It is possible to hide successfully matched rows by using `hide matching rows`.

    !|Compare Stored Queries Hide Matching Rows|fromtable|fromdual|
    |name                                      |n?                |

#### Performance considerations

For better performance when comparing large data sets it's recommended to sort the inputs in ascending order of the comparison key columns.

    !|Store Query|select * from big_table1 order by name|query1|

    !|Store Query|select * from big_table2 order by name|query2|

    !|Compare Stored Queries|query1|query2|
    |name                   |n?           |

If it's needed to compare really huge sets: it's most likely best to push down the heavy comparison to the backend database instead of doing it in `DbFit`.
