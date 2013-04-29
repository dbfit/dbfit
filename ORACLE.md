#### Installing Oracle XE

*Note: These instructions are work in progress.*

 1. Download the `Oracle XE 11g for Linux x64` RPM from [Oracle](http://www.oracle.com/technetwork/products/express-edition/downloads/index.html) from inside the VM.

 2. Install Oracle:

    unzip <rpm-name.rpm>
    sudo yum install <rpm-name.rpm>
    sudo /etc/init.d/oracle-xe configure
    echo ". /u01/app/oracle/product/11.2.0/xe/bin/oracle_env.sh" >> ~/.bashrc

 3. Run setup script:

    sqlplus /nolog @dbfit-java/oracle/src/test/resources/acceptancescripts-Oracle.sql 

 4. Run integration tests to verify setup

    gradle :dbfit-java:oracle:test
