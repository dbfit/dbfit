## Contributing

### Documentation changes

Please raise pull requests against master; the `gh-pages` branch is used only for publishing.

### Code changes

Pull requests are most welcome, but please try to test them with the existing tests first.

#### Fast build

The quickest way to get feedback on your code changes is to run the fast build:

    $ ./gradlew clean fastbuild

This is a pure-Java build that runs the `core` unit tests and the Derby integration tests (with Derby running embedded).

#### Integration tests

If you make changes to any database adapter, it's sufficient to make sure that the tests for only that adapter run eg if you make any changes to the Mysql adapter, you can run the Mysql integration tests:

    $ ./gradlew :dbfit-java:mysql:test

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

**Note: the current setup relies on `nfs` to share the development directory between the host and the guest machines. Currently (May '13) vagrant for Windows doesn't support `nfs`, so this VM setup will only work for Linux/Mac OS.**

### Setting up the test VM

1. You first need to [install VirtualBox](https://www.virtualbox.org/wiki/Downloads).

2. [Install vagrant](http://docs.vagrantup.com/v2/installation/). Versions 1.2+.

3. Install vagrant plugins
   [vagrant-librarian-chef](https://github.com/jimmycuadra/vagrant-librarian-chef),
   [vagrant-omnibus](https://github.com/schisamo/vagrant-omnibus),
   [vagrant-vbguest](https://github.com/dotless-de/vagrant-vbguest):

        vagrant plugin install vagrant-librarian-chef
        vagrant plugin install vagrant-omnibus
        vagrant plugin install vagrant-vbguest

4. If you want to provision Oracle database - download the setup package as
   described in 1st bullet of [ORACLE file](ORACLE.md).

5. Run every subsequent command from the `test_vm` folder:

        cd test_vm

6. Provision and start the vagrant VM:

        vagrant up

### Setting up to build successfully

The subsequent steps need to be followed on the project folder within the VM. To get there:

 1. First, ssh into the machine:

        vagrant ssh

 2. The development directory is NFS-mounted under `/var/dbfit`. Change into it:

        cd /var/dbfit 

#### Installing Oracle XE

If you downloaded Oracle setup package before provisioning vagrant VM - the
Oracle database is automatically installed and configured.

For manual setup instructions see [ORACLE file](ORACLE.md).

#### IDE Integration

##### IntelliJ

An IntelliJ project can be created by running:

 1. `dbfit-java$ ./gradlew idea`

 2. set gradle.java.home=<gradle jdk path> in $IDEA_HOME/bin/idea.properties    

##### Eclipse

An Eclipse project can be created by running:

    dbfit-java$ ./gradlew eclipse

#### Building

*  Clean, build, test and install to local maven repo

        dbfit-java$ ./gradlew clean check install

*  Build and package all java projects:

        dbfit$ ./gradlew assemble

*  Create release zip

        dbfit$ ./gradlew bundle

*  Start fitnesse as a webservice on http://localhost:8085

        dbfit$ ./gradlew start

This will compile the source, copy the jars and resources to the `dist` directory and run up FitNesse with DbFit installed.  Point your browser to [http://localhost:8085](http://localhost:8085) to access the running instance (please adjust `localhost` to match the host on which the instance is actually running).

Please be aware that if you change any code whilst the `/.gradlew start` command is running you will have to stop the command and re-run it in order to compile and pick up the changes.  To stop the running instance it is preferable to point your browser to [http://localhost:8085/?shutdown](http://localhost:8085/?shutdown) rather than killing it at the command line, i.e. don't do a Ctrl+C or equivalent (again adjust `localhost` to match the host on which the instance is actually running).

### Useful development commands

 *  Logging in as `root` for `mysql`:

        mysql -u root -ppassword

 *  Logging in as the `postgres` superuser for `postgresql`:

        sudo su postgres
        psql dbfit

 *  Logging in as the `system` superuser for `oracle`:

        sqlplus system/oracle

### Adding a new database adapter

Let's say that you wish to implement a DbFit driver for an as-yet-unsupported database, Newdata. Here is a broad outline of the steps that you would need to take:

1. Implement a `NewdataEnvironment` class, which implements the [`DBEnvironment` interface](dbfit-java/core/src/main/java/dbfit/api/DBEnvironment.java) and is annotated with `@DatabaseEnvironment`. You will almost certainly want to subclass [`AbstractDbEnvironment`](dbfit-java/core/src/main/java/dbfit/api/AbstractDbEnvironment.java), as that already does a lot of the work. Fundamentally, you will have to provide implementations for the following:
    - a mapping between the Newdata database types and `java.sql` datatypes
    - a query that yields all column metadata given the name of a table or view
    - a query that yields all parameters given the name of a stored procedure or function
2. Implement a `NewdataTest` class which extends [`DatabaseTest`](dbfit-java/core/src/main/java/dbfit/DatabaseTest.java) - this is the fixture class that you will use to initialize your database code in your DbFit tests.
3. Clone the Oracle DbFit acceptance test suites - [FlowMode](FitNesseRoot/DbFit/AcceptanceTests/JavaTests/OracleTests/FlowMode/) and [StandaloneFixtures](FitNesseRoot/DbFit/AcceptanceTests/JavaTests/OracleTests/StandaloneFixtures/) - and convert them to use the Newdata database. When these tests pass, that is the clearest indication that your implementation is working. Not all of the tests will be applicable, and can be ignored.
4. Add any extra tests for any Newdata-specific SQL features that you may wish to use, and have to write code to provide the specific support.

When in doubt, have a look how the problem you're having is solved in one of the several other database drivers.
