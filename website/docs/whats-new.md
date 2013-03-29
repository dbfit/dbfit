---
layout: page
title: "What's New"
nav_bar_name: docs
show_comments: false
---
## In 2.0.0 RC4 <small>28th Mar 2013</small>

 *  Features:
     *  Support for using the Oracle 11g JDBC driver (issues [#67](https://github.com/benilovj/dbfit/pull/67), [#56](https://github.com/benilovj/dbfit/pull/56), [#54](https://github.com/benilovj/dbfit/issues/54), [#53](https://github.com/benilovj/dbfit/issues/53), [#28](https://github.com/benilovj/dbfit/issues/28))
 *  Documentation improvements:
     *  Reinstated [What's wrong with xUnit](http://benilovj.github.com/dbfit/docs/whats-wrong-with-xunit.html)
     *  Added the [DbFit test catalog](http://benilovj.github.com/dbfit/docs/writing-tests.html)
 *  Bugfixes:
     *  Exception for stored procedure input parameters of type NUMBER in standalone mode ([#58](https://github.com/benilovj/dbfit/issues/58))
     *  Synonyms followed properly when using Oracle ([#52](https://github.com/benilovj/dbfit/pull/52), [#50](https://github.com/benilovj/dbfit/issues/50))
     *  Derby: inconsistent handling of case across different fixtures ([#22](https://github.com/benilovj/dbfit/issues/22))
     *  Inconsistent syntax for calling stored proc without parameters ([#21](https://github.com/benilovj/dbfit/issues/21))

For the full list of solved issues, see the [milestone on GitHub](https://github.com/benilovj/dbfit/issues?milestone=3&state=closed).

<hr/>

## In 2.0.0 RC3 <small>5th Mar 2013</small>

 *  Bugfixes:
     *  `Execute procedure` with no parameters in standalone mode now works ([issue #19](https://github.com/benilovj/dbfit/issues/19)).
     *  Remove inconsistencies in `Execute procedure` calls ([issue #21](https://github.com/benilovj/dbfit/issues/21)).

<hr/>

## In 2.0.0 RC2 <small>26th Feb 2013</small>

 *  DbFit upgraded to FitNesse release v20130216:
     *  WISYWIG editor: multiline cell editing bugfixes.

 *  DbFit now bundled with [FitSharp 2.2 (.NET 4.0)](http://www.syterra.com/FitSharp.html). This:
     *  allows combining DbFit and .NET fixtures in the same test
     *  expands database supports in .NET mode to MS Sql Server, Oracle, MySQL and Sybase

 *  Support for the Oracle NCLOB datatype ([issue #14](https://github.com/benilovj/dbfit/issues/14)).

 *  The DbFit Acceptance Test pages have been moved to a subwiki ([issue #15](https://github.com/benilovj/dbfit/pull/15)).

<hr/>

## In 2.0.0 RC1 <small>16th Jan 2013</small>

 *  Even more databases supported. The full list:
     *  Oracle
     *  Microsoft SQL Server
     *  DB2
     *  Derby
     *  HSQLDB
     *  MySQL
     *  PostgreSQL

 *  DbFit upgraded to [FitNesse](http://fitnesse.org) release v20121220 which includes:
     *  a refresh of the user interface
     *  a snazzy WISYWIG editor (which makes page editing a lot easier)
     *  a slew of FitNesse fixes

 *  [New home on GitHub](https://benilovj.github.com/dbfit/) for the project sources and documentation

*Note: this release only includes Java fixtures*
