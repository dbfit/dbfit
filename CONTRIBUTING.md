## Contributing

### Documentation changes

Please raise pull requests against master; the `gh-pages` branch is used only for publishing.

### Code changes

Pull requests are most welcome, but please try to test them with the existing tests.

The easiest way to get a DbFit test environment is to provision a Linux virtual machine image. The instructions below describe how to do this.

The fully-built VM includes:

 *  working installs of Gradle, MySQL, PostgreSQL, Derby and HSQLDB
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

#### Installing Oracle XE

*Note: These instructions are work in progress.*

 1. Download the `Oracle XE 11g for Linux x64` RPM from [Oracle](http://www.oracle.com/technetwork/products/express-edition/downloads/index.html) from inside the VM.

 2. Install the RPM:
        
        sudo yum install <rpm-name.rpm>

 3. TODO...

#### Building

1.  Install the root project POM into the local Maven repo:
    
        dbfit-java$ gradle clean install

2.  Install the `dbfit-core`:

        core$ gradle install

3.  Build and package all the subprojects:

        dbfit-java$ gradle assemble

### Useful development commands

 *  Logging in as `root` for `mysql`:

        mysql -u root

 *  Logging in as the `postgres` superuser for `postgresql`:

        sudo su postgres
        psql dbfit

 *  Logging in as the `system` superuser for `oracle`:

        sqlplus system/system
