## Contributing

### Documentation changes

Please raise pull requests against master; the `gh-pages` branch is used only for publishing.

### Code changes

Pull requests are most welcome, but please try to test them with the existing tests first.

#### Fast build

The quickest way to get feedback on your code changes is to run the fast build:

    $ gradle clean fastbuild

This is a pure-Java build that runs the `core` unit tests and the Derby integration tests (with Derby running embedded).

#### Integration tests

If you make changes to any database adapter, it's sufficient to make sure that the tests for only that adapter run eg if you make any changes to the Mysql adapter, you can run the Mysql integration tests:

    $ gradle :dbfit-java:mysql:test

If you have to make changes to `core`, please run all integration tests (because `core` changes can affect any of the adapters). This is easiest done from the test virtual machine.

#### Integration tests without a VM

You can set up an integration test environment without a VM by:
 *  installing the appropriate database locally
 *  executing the SQL scripts found in `src/test/resources` of the respective DB driver

However, unlike the VM, this approach doesn't necessarily create all the users and permissions needed for the tests.

### The test virtual machine

The easiest way to get a DbFit test environment is to provision a Linux virtual machine image. The instructions below describe how to do this.

The fully-built VM includes:
 *  working installs of Gradle, MySQL, PostgreSQL, Derby and HSQLDB
 *  the pre-requisites to easily install Oracle

The VM doesn't include:
 *  a working Oracle installation (however there is a shell script to help with the installation described below)
 *  a working DB2 installation
 *  a working SQL Server installation (obviously)

### Setting up the test VM

*Note: the following instructions are Mac OS/Linux specific but the same approach should work under Windows as well, since vagrant and VirtualBox run on Windows.*

1.  You first need to [install VirtualBox](https://www.virtualbox.org/wiki/Downloads).

2.  You need to have ruby installed, version 1.8.7, 1.9.X or 2.0.x. The Windows installed can be found [here](http://rubyinstaller.org/downloads/).

3. [Install vagrant](http://docs.vagrantup.com/v2/installation/). Versions 1.1+ are preferable.
   (If for some reason 1.0.x should be used - add it as a dependency to Gemfile)

4.  Run every subsequent command from the `test_vm` folder:

        cd test_vm

5.  Install ruby `bundler`:

        sudo gem install bundler

6.  Install the necessary ruby gems (including `vagrant`):

        bundle install

7.  Install the `vagrant` recipes:

        bundle exec librarian-chef install

8.  Provision and start the vagrant VM:

        vagrant up

### Setting up to build successfully

The subsequent steps need to be followed on the project folder within the VM. To get there:

 1. First, ssh into the machine:

        vagrant ssh

 2. The development directory is NFS-mounted under `/var/dbfit`. Change into it:

        cd /var/dbfit 

#### Installing Oracle XE

See the [ORACLE file](ORACLE.md) file 

#### IDE Integration

##### IntelliJ

An IntelliJ project can be created by running:

 1. dbfit-java$ gradle idea

 2. set gradle.java.home=<gradle jdk path> in $IDEA_HOME/bin/idea.properties    

##### Eclipse

An Eclipse project can be created by running:

    dbfit-java$ gradle eclipse

#### Building

*  Clean, build, test and install to local maven repo
    
        dbfit-java$ gradle clean check install

*  Build and package all java projects:

        dbfit$ gradle assemble

*  Create release zip

        dbfit$ gradle bundle

*  Start fitness as a webservice on http://localhost:8085

        dbfit$ gradle start

### Useful development commands

 *  Logging in as `root` for `mysql`:

        mysql -u root

 *  Logging in as the `postgres` superuser for `postgresql`:

        sudo su postgres
        psql dbfit

 *  Logging in as the `system` superuser for `oracle`:

        sqlplus system/oracle

### Adding a new database adapter

Let's say that you wish to implement a DbFit driver for an as-yet-unsupported database, Newdata. Here is a broad outline of the steps that you would need to take:

1.  Implement a `NewdataEnvironment` class, which implements the [`DBEnvironment` interface](dbfit-java/core/src/main/java/dbfit/api/DBEnvironment.java) and is annotated with `@DatabaseEnvironment`. You will almost certainly want to subclass [`AbstractDbEnvironment`](dbfit-java/core/src/main/java/dbfit/api/AbstractDbEnvironment.java), as that already does a lot of the work. Fundamentally, you will have to provide implementations for the following:

    - a mapping between the Newdata database types and `java.sql` datatypes
    - a query that yields all column metadata given the name of a table or view
    - a query that yields all parameters given the name of a stored procedure or function

2.  Implement a `NewdataTest` class which extends [`DatabaseTest`](dbfit-java/core/src/main/java/dbfit/DatabaseTest.java) - this is the fixture class that you will use to initialize your database code in your DbFit tests.
3.  Clone the Oracle DbFit acceptance test suites - [FlowMode](FitNesseRoot/DbFit/AcceptanceTests/JavaTests/OracleTests/FlowMode/) and [StandaloneFixtures](FitNesseRoot/DbFit/AcceptanceTests/JavaTests/OracleTests/StandaloneFixtures/) - and convert them to use the Newdata database. When these tests pass, that is the clearest indication that your implementation is working. Not all of the tests will be applicable, and can be ignored.
4.  Add any extra tests for any Newdata-specific SQL features that you may wish to use, and have to write code to provide the specific support.

When in doubt, have a look how the problem you're having is solved in one of the several other database drivers.
