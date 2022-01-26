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

2. Configure the connection for the DbFit acceptance tests in the file `dbfit-java/teradata/TestDbConnectionDbFitTeradata.properties.custom`:
   * Set the user name in the `username=` key.
   * Set the password in the `password=` key.
   * Set the hostname and the port number (format is `<hostname>:<port>`) in the `service=` key.

3. Create the test database by running the command `./gradlew :dbfit-java:teradata:setUpTestDatabase`.
  * **NOTE:** The test database can be destroyed with the command `./gradlew :dbfit-java:teradata:tearDownTestDatabase`.

4. Run the Teradata DbFit acceptance tests with `./gradlew :dbfit-java:teradata:integrationTest`.
