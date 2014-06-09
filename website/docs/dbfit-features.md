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
      <li><a href="#database-password-encryption">Database password encryption</a></li>
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
| Teradata   | x          |                |
{: class="table table-bordered"}

----

## Writing tests in Excel

Although FitNesse Wiki syntax is really simple, you do not have to use it to write scripts. You can write your tables in Excel (or almost any other spreadsheet program), and then just copy them into the FitNesse page editor. Clipboard automatically picks up data from most spreadsheet programs in tab-separated format, which can be directly converted to FitNesse with the `Spreadsheet to FitNesse` button that is available when editing a page. If your spreadsheet program behaves differently, it should be able to export tab-separated files.

You can also convert a FitNesse table to tab-separated data with the `FitNesse to Spreadsheet` button in page editor, and then copy that into Excel for editing.

<img class="img-polaroid" src="/dbfit/docs/screenshots/excel-editing.png">

----

## Database password encryption

DbFit has [several ways to connect to the database](/dbfit/docs/reference.html#connect). If you are working in an environment where you aren't allowed to store database passwords in plaintext, you may wish to use the `encrypt` utility that ships with DbFit to encrypt the password.

A new *keystore* is created when `encrypt` is invoked for the first time. The default *keystore* filename is:

    $HOME/.dbfit.jks

(`%HOME%` under Windows).

It's possible to create the KeyStore with other tools (eg java `keytool`). In order to keep it compatible with DbFit - the same KeyStore format, key algorithm, alias and keystore password should be used. DbFit can only decrypt the password only if the same keystore was used to encrypt it. If the *keystore* file has been lost or destroyed - it will be impossible to decrypt the passwords generated with it.

### How it works

You encrypt a password using `encrypt` and use the encrypted string as password when configuring your DbFit connection settings. When a test is executed, DbFit decrypts the password and uses it to connect to the database. Both `encrypt` and DbFit use a cryptographic key, which is stored in a password-protected file known as *keystore*.

<div class="alert alert-warning alert-block">If an attacker is able to gain access to the <em>keystore</em>, they would be able to decrypt your passwords, since the <em>keystore</em> is protected by password that is hard-coded into the DbFit binary. Keep your <em>keystore</em> safe by setting restrictive permissions on the file.
</div>

### Usage

`encrypt` is provided both as a shell script (`encrypt.sh`) and a Windows batch file (`encrypt.bat`); the shell script will be used in the examples below.

1.  Encrypt your password

        encrypt.sh your-password-here

    Output:

        ...
        Encrypted Password:
        ENC(jzDH1fYetwCp3JFfAeKebA==)

2.  Copy the encrypted string into the database connection properties file

        ...
        password=ENC(jzDH1fYetwCp3JFfAeKebA==)
        ...


    No change is needed in the DbFit tests - the [`ConnectUsingFile`](/dbfit/docs/reference.html#connect-using-file) and [`Connect`](/dbfit/docs/reference.html#connect) fixtures work with both encrypted and non-encrypted passwords.

#### Using an alternative *keystore* location

To place the *keystore* in an alternative location, specify the `-keyStoreLocation` when invoking `encrypt`:

    encrypt.sh your-password-here -keyStoreLocation <some-path>

To allow the DbFit tests to decrypt passwords using the alternative path, you need to override the FitNesse `COMMAND_PATERN` used for the execution of your tests (you can read more about FitNesse test execution [here](http://www.fitnesse.org/FitNesse.UserGuide.CustomizingTestExecution)). This basically means that you need to add the following line in a parent page of your testsuite (ideally next to the line `!path lib/*.jar`):

    !define COMMAND_PATTERN {java -Ddbfit.keystore.path=<some-path> -cp %p %m}

</div>
  </div>
</div>
