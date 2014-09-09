---
layout: wide
title: Getting Started
nav_bar_name: docs
show_comments: false
---
<div class="row">
  <div class="sidebar span3">
    <ul id="sidenav" class="nav nav-list affix">
      <li class="active"><a href="#installing-dbfit">1. Installing DbFit</a></li>
      <li><a href="#creating-a-new-test-page">2. Creating a new test page</a></li>
      <li><a href="#setting-up-the-environment">3. Setting up the environment</a></li>
      <li><a href="#connecting-to-the-database">4. Connecting to the database</a></li>
      <li><a href="#testing-a-simple-query">5. Testing a simple query</a></li>
      <li><a href="#running-the-test">6. Running the test</a></li>
    </ul>
  </div>
  <div class="span9">
    <div class="page-header">
      <h1>{{ page.title }}</h1>
    </div>
    <div markdown="1">

## 1. Installing DbFit

<div class="alert alert-info alert-block">
  DbFit needs the Java Runtime Environment to run - it can be downloaded from the <a href="http://www.java.com/en/download/index.jsp">Oracle's Java homepage</a>.
</div>

 1. <a class="btn btn-success" href="{{ site.repository }}/releases/download/v{{ site.dbfit_version }}/dbfit-complete-{{ site.dbfit_version }}.zip" onclick="recordOutboundLink(this, 'Software', '{{ site.dbfit_version }}', 'Getting started');return false;">Download DbFit</a>

 2. Unpack `dbfit-complete-XXX.zip` somewhere on your disk, and run `startFitnesse.bat` (or `startFitnesse.sh` on Linux). When FitNesse starts, you should see a command window with this message:

        FitNesse (v20121220) Started...
                port:              8085
                root page:         fitnesse.wiki.FileSystemPage at ./FitNesseRoot
                logger:            none
                authenticator:     fitnesse.authentication.PromiscuousAuthenticator
                page factory:      fitnesse.responders.PageFactory
                page theme:        fitnesse_straight
                page version expiration set to 0 days.

 3. Open [http://localhost:8085/](http://localhost:8085/) and you should see the welcome page. FitNesse is up and running. When you want to shut it down later, just press `Ctrl+C` in the command window (or close the command window).

    <div class="alert alert-error alert-block">
      <h4>FitNesse.bat failed. What's wrong?</h4>

      <p>Read the exception from the command window. If the error mentions versions, check that you have Java 5 or higher installed and that the correct version is being executed when you run <code>java.exe</code>. Run <code>java.exe -version</code> from a command window to see which version of Java is being executed by default. You can run FitNesse with a different Java version either by pointing to the correct JRE in the system executable path (right-click My Computer, select Properties, then go to the Advanced tab, click Environment Variables, and edit the Path variable), or by entering the full path to a different <code>java.exe</code> in <code>startFitnesse.bat</code>.</p>

      <p>If the error report states that there is a security problem or the port is unavailable, enter a different port number in <code>startFitnesse.bat</code> and try again.</p>
    </div>

Let's run a quick test to make sure that you have everything set up correctly and that FitNesse can connect to your test database.

## 2. Creating a new test page

Open [http://localhost:8085/HelloWorldTest](http://localhost:8085/HelloWorldTest) in your browser. You should see an editor - this is where we'll create our new test page.

<span class="label label-info">Note</span> Notice that the page name is a CamelCase word.

<div class="alert alert-warning alert-block">
  In FitNesse, all page names have to start with a capital letter, have at least one more capital letter, and all capital letters have to be separated by at least one lowercase letter. FitNesse is really strict about that. This convention causes a lot of headaches for FitNesse newbies, but after a while you'll get used to it. Here are some good page names:
  <ul>
    <li>HelloWorld</li>
    <li>TestFluxCapacitor</li>
    <li>IsPaymentWorkingCorrectly</li>
  </ul>
  Here are some page names that will get you in trouble:
  <ul>
    <li>helloworld (no capital letters)</li>
    <li>Testfluxcapacitor (just one capital letter)</li>
    <li>isPaymentWorkingCorrectly (starts with a lowercase letter)</li>
    <li>TestFCapacitor (two consecutive capital letters)</li>
  </ul>
</div>

## 3. Setting up the environment

In order to load the DbFit extension into FitNesse, your test pages have to load the correct libraries. Replace the contents of the big textbox with the following:

    !path lib/*.jar

## 4. Connecting to the database

DbFit requires two commands to connect to the database. The first line specifies the database type (or test type), and the second defines connection properties. These two lines will typically be the first on every test page. Here is how to connect to a MySQL database:

    !|dbfit.MySqlTest|

    !|Connect|localhost|dbfit_user|password|dbfit|

Notice the `MySqlTest` in the first line above. That tells DbFit which type of database driver to use.

Here's a comprehensive list of available types:

| Database                  | DbFit Database Type |
|:--------------------------|:--------------------|
| Oracle                    | OracleTest          |
| SQL Server 2005 and later | SqlServerTest       |
| MySQL                     | MySqlTest           |
| Postgres                  | PostgresTest        |
| Derby                     | DerbyTest           |
| HSQLDB                    | HSQLDBTest          |
| DB2                       | DB2Test             |
| DB2i                      | DB2iTest            |
| Teradata                  | TeradataTest        |
| Netezza                   | NetezzaTest         |
{: class="table table-bordered"}

<div class="alert alert-info alert-block">
  Notice how each command starts with an exclamation mark (<code>!</code>), followed by a pipe symbol (<code>|</code>). Command arguments are then separated by the pipe symbol as well. In FitNesse, tables are used to describe commands, tests, inputs and expected results (you will see the table when the page is saved). In the FitNesse wiki syntax, tables are described simply by separating cells with the pipe symbol. The exclamation mark before the first row of the table is optional, and tells FitNesse not to apply any smart formatting to table contents.
</div>


## 5. Testing a simple query

Now let's write a simple query test. We will send a request to the database, pull out the result set, and compare it with our expectations. In DbFit, that is done with the `Query` command. The second cell of the first table row, after the `Query` keyword, should contain the query we are executing. The second row then contains the result set structure — names of the columns that we want to inspect. You don't have to specify the full result set here, just the columns that are interesting for a particular test. All rows after that contain expected results. Query disregards result set order — if the order is important you can use OrderedQuery. Here is a simple MySql query:

    !|Query| select 'test' as x|
    |x|
    |test|

The same syntax should work for SQLServer. For Oracle, use this table:

    !|Query| select 'test' as x from dual|
    |x|
    |test|

## 6. Running the test

Now, click Save. FitNesse will create a new page and display it in your browser. Click on the `Test` button to make FitNesse run the test. You should see a page telling you that the test passed.

----

*Learn about more DbFit commands in the [Reference](/dbfit/docs/reference.html) docs.*

</div>
  </div>
</div>
