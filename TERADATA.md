#### Installing Teradata Express Edition

There are two options for using Teradata Express Edition (TDE):
* A VMWare Linux virtual machine.
* Amazon EC2.

1. If using the VMWare Linux VM:
   * Download VMWare player
     [VMWare](https://my.vmware.com/web/vmware/downloads)
   * Download the Teradata Express Edition VM image
     [Teradata](https://downloads.teradata.com/download/database/teradata-express/vmware)
     **NOTE:** you will need to register an create an account on www.teradata.com.
     (e.g. `TDExpress14.10.01_Sles11_40GB.7z`).
   * Unpack the VMWare image (e.g. using 7zip).
   * Start VMWare Player and open the VM image (the .vmx file).
     **NOTE:** you might want to consider reducing the amount of virtual RAM the for the VM.
               1GB should be sufficient for a DbFit test Teradata server.
               The RAM allocation can be altered from
                  Player -> Manage -> Virtual Machine Settings -> Hardware -> Memory
     **NOTE:** if you need to logon to the VM use user name `root` (password `root`).

2. Download, unpack and install Teradata Studio Express on the DbFit development host:
   * [Teradata](https://downloads.teradata.com/download/tools/teradata-studio-express)
   (e.g. TeradataStudioExpress__linux_x86_64.15.00.00.00-1.tar.gz)

3. Start Teradata Studio Express and connect to the Teradata VM. Use user name ```dbc`` (password `dbc`).

4. Open and execute the Teradata acceptance tests database objects setup script:
   * dbfit-java/teradata/src/test/resources/acceptancescripts-teradata.sql
