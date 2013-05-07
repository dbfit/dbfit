---
layout: wide
title: DbFit Features
nav_bar_name: docs
show_comments: false
---
<div class="row">
  <div class="sidebar span3">
    <ul id="sidenav" class="nav nav-list affix">
      <li class="active"><a href="#database-support">Database support</a></li>
      <li><a href="#writing-tests-in-excel">Writing tests in Excel</a></li>
    </ul>
  </div>
  <div class="span9">
    <div class="page-header">
      <h1>{{ page.title }}</h1>
    </div>
    <div markdown="1">
<span class="label label-info">Note</span>
This page is very much a work in progress at the moment, and more features will be appearing here shortly.

## Database support

There are two ways to run DbFit - through Java or through .NET. As a database developer, you __do not have to know Java or .NET to write and run the tests__. The only significant difference between the two implementations is that the Java and .NET versions support different databases.

|            | DbFit/Java | DbFit/FitSharp |
|------------|:----------:|:--------------:|
| Oracle     | x          | x              |
| SQL Server | x          | x              |
| MySQL      | x          | x              |
| Postgres   | x          |                |
| Derby      | x          |                |
| HSQLDB     | x          |                |
| Sybase     |            | x              |
| DB2        | x          |                |
| Teradata   | *          |                |
{: class="table table-bordered"}

_* The DbFit support for Teradata is experimental_

----

## Writing tests in Excel

Although FitNesse Wiki syntax is really simple, you do not have to use it to write scripts. You can write your tables in Excel (or almost any other spreadsheet program), and then just copy them into the FitNesse page editor. Clipboard automatically picks up data from most spreadsheet programs in tab-separated format, which can be directly converted to FitNesse with the `Spreadsheet to FitNesse` button that is available when editing a page. If your spreadsheet program behaves differently, it should be able to export tab-separated files.

You can also convert a FitNesse table to tab-separated data with the `FitNesse to Spreadsheet` button in page editor, and then copy that into Excel for editing.

<img class="img-polaroid" src="/dbfit/docs/screenshots/excel-editing.png">

</div>
  </div>
</div>
