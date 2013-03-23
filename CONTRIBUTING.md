## Contributing

### Documentation changes

Please raise pull requests against master; the `gh-pages` branch is used only for publishing.

### Code changes

Pull requests are most welcome, but please try to test them with the existing tests.

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

#### The Oracle JDBC Driver

1.  Download the Oracle 11gR2 11.2.0.3 Thin driver (ojdbc6.jar) from the [Oracle homepage](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html).

2.  From the VM, install it into maven:

        mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle \
            -DartifactId=ojdbc6 -Dversion=11.2.0.3.0 -Dpackaging=jar

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
