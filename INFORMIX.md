#### Installing Informix Developer Edition

*Note: Vagrant provisioning can automate the Informix installation - just
 step (1) here is needed to prepare for it. Next continue as described in
 [Contribution Notes](CONTRIBUTING.md).

 If Informix has been skipped from the initial vagrant provisioning but you still
 want to install it - then follow all the steps below.*

1. Download the `Informix Developer Edition for Linux x86_64` TAR from
   [IBM](https://www-01.ibm.com/marketing/iwm/iwm/web/reg/pick.do?source=ifxids&S_TACT=109HF16W&lang=en_US)
   (e.g. `iif.12.10.FC5DE.linux-x86_64.tar`) and place it in the git root directory with the name `ids-linux-x86_64.tar`.

2. Install Informix (this may take a while to complete)

        sudo dbfit-java/informix/src/integration-test/resources/install_informix.sh

3. Run integration tests to verify setup

        ./gradlew clean :dbfit-java:informix:integrationTest
