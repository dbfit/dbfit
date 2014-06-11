## Contributing

### Documentation changes

Please raise pull requests against master; the `gh-pages` branch is used only for publishing.

### Code changes

Pull requests are most welcome, but please try to test them with the existing tests first.

#### Conventions

##### End of line style
DbFit is using Unix style end of line (`LF`) in source files.

Be careful with that on Windows where the default end of line style is `CR+LF`.
If your editor or other tools don't have good support for the Unix end-of-line conventions you may consider using the help of Git's [autocrlf option](http://git-scm.com/book/en/Customizing-Git-Git-Configuration). If you do so, be aware that DbFit tests and VM setup scripts rely on having some files unchanged. So it's advised that you use `autocrlf = input` instead of `autocrlf = auto`.

If your tools have good support for Unix end-of-line format - it's perfectly OK to run with autocrlf unconfigured (or `autocrlf = false`).

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
 *  the pre-requisites to easily install DB2

The VM doesn't include:
 *  a working Oracle installation (however there is a shell script to help with the installation described below)
 *  a working SQL Server installation (obviously)
 *  a working Teradata installation (this can be created separately as a VMWare or EC2 installation)

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

5. If you want to provision DB2 database - download the setup package as
   described in 1st bullet of [DB2 file](DB2.md).
   
6. Run every subsequent command from the `test_vm` folder:

        cd test_vm

6. Provision and start the vagrant VM:

        vagrant up

---

*Note:*
If provisioning fails, you may need to customize some settings according to the specifics of your environment. Please take a look at [`Vagrantfile`](test_vm/Vagrantfile) and [`vagrant_config_custom.rb.sample`](test_vm/vagrant_config_custom.rb.sample). The latter provides instruction how Vagrantfile settings can be customized.

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

#### Installing DB2 Express-C

If you downloaded DB2 setup package before provisioning vagrant VM - the
DB2 database is automatically installed and configured.

For manual setup instructions see [DB2 file](DB2.md).

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

  *Keep in mind that this runs from the transient `dist` folder. Any wiki page edits that you make in this mode will be wiped when the next build tasks are run.*


*  Start fitnesse on top of actual source of DbFit acceptance tests (can be used to edit the tests)

        dbfit$ ./gradlew starthere

This will compile the source, copy the jars and resources to the `dist` directory and run up FitNesse with DbFit installed.  Point your browser to [http://localhost:8085](http://localhost:8085) to access the running instance (please adjust `localhost` to match the host on which the instance is actually running).

Please be aware that if you change any code whilst the `/.gradlew start` command is running you will have to stop the command and re-run it in order to compile and pick up the changes.  To stop the running instance it is preferable to point your browser to [http://localhost:8085/?shutdown](http://localhost:8085/?shutdown) rather than killing it at the command line, i.e. don't do a Ctrl+C or equivalent (again adjust `localhost` to match the host on which the instance is actually running).


*  By default the uncommitted acceptance tests are being purged when preparing the new content of `dist` directory. In order to keep them, `keepTests` project property may be used

        dbfit$ ./gradlew starthere -PkeepTests

#### Using custom libraries

If you need to use libraries which are not available on the public artifact repositories (e.g. proprietary JDBC drivers) - you may place them in `custom_libs` directory. This folder is configured as [flat directory repository](http://www.gradle.org/docs/current/userguide/dependency_management.html#sec:flat_dir_resolver) in Gradle - the typical naming format of JARs inside is `<main_name>-<version>.jar`.

### Useful development commands

 *  Logging in as `root` for `mysql`:

        mysql -u root -ppassword

 *  Logging in as the `postgres` superuser for `postgresql`:

        sudo su postgres
        psql dbfit

 *  Logging in as the `system` superuser for `oracle`:

        sqlplus system/oracle

 *  Logging in as the `db2inst1` superuser for `db2`:

        sudo su - db2inst1
        db2

### Installing Teradata Express Edition

For manual setup instructions see [TERADATA file](TERADATA.md).

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
