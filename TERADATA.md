#### Installing Teradata Express Edition

There are two options for hosting the Teradata Express Edition (TDE) Linux VM:
* A local VMWare installation.
* Amazon EC2.

1. If using the VMWare Linux VM:
   * Download [VMWare](https://my.vmware.com/web/vmware/downloads) Player.
   * Download the [Teradata](https://downloads.teradata.com/download/database/teradata-express/vmware) Express Edition VM image
     (e.g. `TDExpress14.10.01_Sles11_40GB.7z`).
     
     **NOTE:** you will need to register and create an [account](https://downloads.teradata.com/user/login) on www.teradata.com.
     
   * Unpack the VMWare image (e.g. using 7zip).
   * Start VMWare Player and open the VM image (the .vmx file).
   
     **NOTE:** you might want to consider reducing the amount of virtual RAM the for the VM.
               1GB should be sufficient for a DbFit test Teradata server.
               The RAM allocation can be altered from
                  Player -> Manage -> Virtual Machine Settings -> Hardware -> Memory
     
     **NOTE:** if you need to logon to the VM use user name `root` (password `root`).

2. Download, unpack and install [Teradata](https://downloads.teradata.com/download/tools/teradata-studio-express) Studio Express on the DbFit development host.
   (e.g. `TeradataStudioExpress__linux_x86_64.15.00.00.00-1.tar.gz`)

3. Start Teradata Studio Express and connect to the Teradata VM. Use user name `dbc` (password `dbc`).

4. Download and unpack the [Teradata](https://downloads.teradata.com/download/connectivity/jdbc-driver) JDBC driver.
   (e.g. `TeraJDBC__indep_indep.15.00.00.15.zip`)
   
   Create the `custom_libs` directory in the git root directory and copy the driver files `terajdbc4.jar` and `tdgssconfig.jar` into it.

4. Open and execute the Teradata acceptance tests database objects setup script:
   * dbfit-java/teradata/src/test/resources/acceptancescripts-teradata.sql

5. Run integration tests to verify setup

        ./gradlew :dbfit-java:teradata:test