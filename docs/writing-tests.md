---
layout: page
title: Writing Tests
nav_bar_name: docs
show_comments: false
---
## The Arrange-Act-Assert Test

### Example

    !|insert|source   |
    |col1   |col2|col3|
    |val1   |val2|val3|

    |execute procedure|some_procedure|
    |param1           |param2        |
    |val1             |val2          |

    |query   |select * from target|
    |col1    |col2    |col3       |
    |exp_val1|exp_val2|exp_val3   |

### Description

This test type has 3 basic stages:

 1. Set up the input data (arrange).
 2. Execute a function or procedure (act).
 3. Run a query and compare actual vs expected data (assert).

### When is it useful?

 *  Ideally, as part of your test-driven-development cycle, or
 *  While unit-testing your transformation/procedure, or 
 *  For functional testing of your transformation.

### Pre-requisites

An development environment where it's OK to overwrite or truncate data.

### Useful table types

 *  [Insert](/dbfit/docs/reference.html#insert)
 *  [Update](/dbfit/docs/reference.html#update)
 *  [Execute procedure](/dbfit/docs/reference.html#execute-procedure)
 *  [Query](/dbfit/docs/reference.html#query)
 *  [Inspect query](/dbfit/docs/reference.html#inspect)
