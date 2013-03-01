# DbFit

DbFit is a set of fixtures which enables FIT/FitNesse tests to execute directly against a database.

## Contributing

The easiest way to get a DbFit test environment is to provision a Linux virtual machine image. The instructions below describe how to do this.

The fully-built VM includes:

 *  working installs of Maven, MySQL, PostgreSQL, Derby and HSQLDB
 *  the pre-requisites to easily install Oracle

The VM doesn't include:
 *  a working Oracle installation (fetching and installing the Oracle binaries and JDBC jar hasn't been automated yet, but is partially described below)
 *  a working DB2 installation
 *  a working SQL Server installation (obviously)

### Setting up the test VM

*Note: the following instructions are Mac OS/Linux specific but the same approach should work under Windows as well, since vagrant and VirtualBox run on Windows.*

1.  You first need to [install VirtualBox](https://www.virtualbox.org/wiki/Downloads). I have been using version 4.1.18. Guest additions are also required.

2.  You need to have ruby installed, version 1.8.7 or 1.9.X (I haven't tested with ruby 2.0). The Windows installed can be found [here](http://rubyinstaller.org/downloads/).

3.  Run every subsequent command from the `test_vm` folder:

        cd test_vm

4.  Install ruby `bundler`:

        sudo gem install bundler

5.  Install the necessary ruby gems (including `vagrant`):

        bundle install

6.  Install the `vagrant` recipes:

        bundle exec librarian-chef install

7.  Provision and start the vagrant VM:

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

#### FitNesse

Because the edge builds of FitNesse don't get pushed to the Maven Repository, it needs to be manually pushed into the local repo.

 1. Download the `fitnesse` jar:

        wget https://cleancoder.ci.cloudbees.com/job/fitnesse/278/artifact/dist/fitnesse.jar

 2. Download the `fitnesse` POM file:

        wget http://repo1.maven.org/maven2/org/fitnesse/fitnesse/20121220/fitnesse-20121220.pom

 3. Update the version string in the POM file:

        sed -i 's/20121220/20130216/g' fitnesse-20121220.pom

 2. Install the jar:

        mvn install:install-file -Dfile=fitnesse.jar -DgroupId=org.fitnesse \
            -DartifactId=fitnesse -Dversion=20130216 -Dpackaging=jar -DpomFile=fitnesse-20121220.pom

#### FitLibrary 

1.  Download the `fitlibrary` jar:

        wget https://s3.amazonaws.com/dbfit/fitlibrary-20081102.jar

2.  Install the fitlibrary JAR [...]

        mvn install:install-file -Dfile=fitlibrary-20081102.jar -DgroupId=org.fitnesse \
            -DartifactId=fitlibrary -Dversion=20081102 -Dpackaging=jar

#### Installing Oracle XE

*Note: These instructions are work in progress.*

 1. Download the `Oracle XE 11g for Linux x64` RPM from [Oracle](http://www.oracle.com/technetwork/products/express-edition/downloads/index.html) from inside the VM.

 2. Install the RPM:
        
        sudo yum install <rpm-name.rpm>

 3. TODO...

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

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/ed067fb4af15878098fbee214e0356af "githalytics.com")](http://githalytics.com/benilovj/dbfit)
