# DbFit

DbFit is a set of fixtures which enables FIT/FitNesse tests to execute directly against a database.

## Contributing

### Setting up the test VM

*Note: the following instructions are Mac OS/Linux specific but the same approach should work under Windows as well, since vagrant and VirtualBox run on Windows.*

1.  You first need to [install VirtualBox](https://www.virtualbox.org/wiki/Downloads). I have been using version 4.1.18. Guest additions are also required.

2.  Run every subsequent command from the `test_vm` folder:

        cd test_vm

3.  Install ruby `bundler`:

        sudo gem install bundler

4.  Install the necessary ruby gems (including `vagrant`):

        bundle install

5.  Install the `vagrant` recipes:

        bundle exec librarian-chef install

6.  Provision and start the vagrant VM:

        bundle exec vagrant up

### Setting up to build successfully

The subsequent steps need to be followed on the project folder within the VM. To get there:

 1. First, ssh into the machine:
        
        vagrant ssh

 2. The development directory is NFS-mounted under `/var/dbfit`. Change into it:

        cd /var/dbfit 

#### The Oracle JDBC Driver

1.  Download the Oracle 10g 10.2.0.2.0 Thin driver (ojdbc14.jar) from the [Oracle homepage](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html).

2.  From the VM, install it into maven:

        mvn install:install-file -Dfile=ojdbc14.jar -DgroupId=com.oracle \
            -DartifactId=ojdbc14 -Dversion=10.2.0.2.0 -Dpackaging=jar

#### FitLibrary 

1.  Download the `fitlibrary` jar:

        wget https://s3.amazonaws.com/dbfit/fitlibrary-20081102.jar

2.  Install the fitlibrary JAR [...]

        mvn install:install-file -Dfile=fitlibrary-20081102.jar -DgroupId=org.fitnesse \
            -DartifactId=fitlibrary -Dversion=20081102 -Dpackaging=jar

#### Building

1.  Install the root project POM into the local Maven repo:
    
        dbfit-java$ mvn -N install

2.  Install the `dbfit-core`:

        core$ mvn install

3.  Build and package all the subprojects:

        dbfit-java$ mvn package

### Useful development commands

 *  Logging in as `root` for `mysql`:

        mysql -u root

 *  Logging in as the `postgres` superuser for `postgresql`:

        sudo su postgres
        psql dbfit

 *  Logging in as the `system` superuser for `oracle`:

        sqlplus system/system

## License

DbFit is released under the [GNU General Public License, version 2](http://www.gnu.org/licenses/gpl-2.0.txt).
