#### Installing and testing with Netezza emulator

The Netezza emulator VM is available as an Open Virtualisation archive (OVA) format file.
It consists of a Red Hat Linux Netezza host image that contains a nested VM for the Netezza SPU.
Because of the nested VM virtualisation of Intel VT-x/EPT or AMD-V/RVI is required. This feature is not currently availale with Oracle VirtualBox hence it is recommended to run the Netezza emulator with VMWare Player/Workstation.

1. Install the Netezza Emulator from IBM Netezza Developer Network
  * Download [VMware](https://my.vmware.com/web/vmware/downloads) Player.
  * Download the 7.2 version of the [Netezza Emulator](https://www14.software.ibm.com/webapp/iwm/web/reg/pick.do?source=swg-im-ibmndn&lang=en_US)
    * **NOTE:** you will need to register and create an IBM developer network account
  * Install VWware 
  * Open the downloaded Netezza VM `.ova` file in VMWare
    * **NOTE:** The Netezza Emulator typically requires >= 4G of ram
    * **NOTE:** the default passwords for the emulator are `nz`/`nz` for the Linux host and `admin`/`password` for the database connection
  
2. Configure the connection for the DbFit acceptance tests in the file `dbfit-java/netezza/TestDbConnectionDbFitNetezza.properties.custom`:
   * Set the user name in the `username=` key.
   * Set the password in the `password=` key.
   * Set the hostname and the port number (format is `<hostname>:<port>`) in the `service=` key.

3. Create the database NETEZZATEST by running the command `./gradlew :dbfit-java:netezza:setUpTestDatabase`.
  * **NOTE:** The test database can be destroyed with the command `./gradlew :dbfit-java:netezza:tearDownTestDatabase`.

4. Run the Netezza DbFit acceptance tests with `./gradlew :dbfit-java:netezza:integrationTest`.
