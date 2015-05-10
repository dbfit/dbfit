#### Installing Teradata Express Edition

There are two options for hosting the Teradata Express Edition (TDE) Linux VM:
* A local VMWare installation.
* Amazon EC2.

1. If using the VMWare Linux VM:
   * Download [VMWare](https://my.vmware.com/web/vmware/downloads) Player.
   * Download the [Teradata](https://downloads.teradata.com/download/database/teradata-express/vmware) Express Edition VM image
     (e.g. `TDExpress14.0.3_Sles10_4GB.7z`).
     
     **NOTE:** you will need to register and create an [account](https://downloads.teradata.com/user/login) on www.teradata.com.
     
   * Unpack the VMWare image (e.g. using 7zip).
   * Start VMWare Player and open the VM image (the .vmx file).
   
     **NOTE:** you might want to consider reducing the amount of virtual RAM the for the VM.
               1GB should be sufficient for a DbFit test Teradata server.
               The RAM allocation can be altered from
                  Player -> Manage -> Virtual Machine Settings -> Hardware -> Memory
     
     **NOTE:** if you need to logon to the VM use user name `root` (password `root`).

2. Log into the Teradata VM as the user `root` (password `root`) and start Teradata Studio Express from the desktop icon.
   Connect to Teradata (on `localhost`) as user `dbc` (password `dbc`) in Teradata transaction mode (`TMODE=TERA`).

3. Open the Teradata acceptance tests database objects setup script from the host `dbfit-java/teradata/src/integration-test/resources/acceptancescripts-teradata.sql`.
   
   Copy and paste the script into a Teradata Studio Express SQL Editor query window on the guest and run the script.
   
   Alternatively you may set up shared folders for the VM to acccess folders on the host from
       Player -> Manage -> Virtual Machine Settings -> Options -> Shared Folders
   
   Or install Teradata Studio Express on the host machine and connect to the VM (the default IP address is `192.168.68.128`).

4. Run integration tests to verify setup

        ./gradlew :dbfit-java:teradata:integrationTest
