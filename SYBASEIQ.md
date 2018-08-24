#### Installing SAP Sybase IQ (evaluation version)

*Note: Vagrant provisioning can automate the Sybase IQ installation - just
 step (1) here is needed to prepare for it. Next continue as described in
 [Contribution Notes](CONTRIBUTING.md).

 If Sybase IQ has been skipped from the initial vagrant provisioning but you still
 want to install it - then follow all the steps below.*

1. Download the SAP IQ Express Edition or Trial version. E.g. from 
   [SAP](https://www.sap.com/cmp/syb/crm-xm13-dtb-dbtch-tr20/index.html)
   (e.g. `Linux64-iq1610sp03_expr.tgz`) and place it in the git root directory with the name `Linux-iq.tgz`.

2. Install Sybase IQ (this may take a while to complete)

        sudo test_vm/scripts/sybase/install_sybaseiq.sh

3. Run integration tests to verify setup

        ./gradlew clean :dbfit-java:sybase:integrationTest

#### Manually stopping the Sybase IQ test server and database

        . /opt/sap/IQ.sh
        dbstop dbfit

#### Manually starting the Sybase IQ test server and database

        sudo test_vm/scripts/sybase/start_sybaseiq_database.sh
