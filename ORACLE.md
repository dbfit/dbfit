#### Installing Oracle XE

*Note: Vagrant provisioning can automate the Oracle installation - just
 step (1) here is needed to prepare for it. Next continue as described in
 [Contribution Notes](CONTRIBUTING.md).

 If Oracle has been skipped from the initial vagrant provisioning but you still
 want to install it - then follow all the steps below.*

1. Download the `Oracle XE 11g for Linux x64` RPM zip from
   [Oracle](http://www.oracle.com/technetwork/database/database-technologies/express-edition/overview/index.html)
   (e.g. `oracle-xe-11.2.0-1.0.x86_64.rpm.zip`) and place it in the git root directory.

2. Install Oracle (this may take a while to complete)

        sudo dbfit-java/oracle/src/integration-test/resources/install_oracle.sh

3. Run integration tests to verify setup

        ./gradlew :dbfit-java:oracle:test

