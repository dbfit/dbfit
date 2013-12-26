#### Installing Oracle XE

*Note: These instructions are work in progress.*

1. Download the `Oracle XE 11g for Linux x64` RPM zip from
   [Oracle](http://www.oracle.com/technetwork/database/database-technologies/express-edition/overview/index.html)
   (e.g. `oracle-xe-11.2.0-1.0.x86_64.rpm.zip`) and place it in the git root directory.

2. Install Oracle:

        sudo dbfit-java/oracle/src/test/resources/install_oracle.sh

3. Run integration tests to verify setup

        ./gradlew :dbfit-java:oracle:test

