#### Installing DB2 Express-C

*Note: Vagrant provisioning can automate the DB2 installation - just
 step (1) here is needed to prepare for it. Next continue as described in
 [Contribution Notes](CONTRIBUTING.md).

 If DB2 has been skipped from the initial vagrant provisioning but you still
 want to install it - then follow all the steps below.*

1. Download the Linux x86-64 `DB2 Express-C for Linux x86-64` GZipped TAR from
   [IBM](https://www14.software.ibm.com/webapp/iwm/web/pick.do?source=swg-db2expressc&S_CMP=db2teamblog)
   (e.g. `v10.5fp1_linuxx64_expc.tar.gz`) and place it in the git root directory with the name `db2_linuxx64_expc.tar.gz`.

2. Install DB2 (this may take a while to complete)

        sudo dbfit-java/db2/src/integration-test/resources/install_db2.sh

3. Run integration tests to verify setup

        ./gradlew :dbfit-java:db2:test

