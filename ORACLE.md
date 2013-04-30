#### Installing Oracle XE

*Note: These instructions are work in progress.*

1. Download the `Oracle XE 11g for Linux x64` RPM from [Oracle](http://www.oracle.com/technetwork/products/express-edition/downloads/index.html) and place it in the git root directory.

2. Install Oracle:

        dbfit-java/oracle/src/test/resources/install_oracle.sh

3. Run integration tests to verify setup

    	gradle :dbfit-java:oracle:test
