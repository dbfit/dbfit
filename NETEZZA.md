#### Installing and testing with Netezza emulator

The Netezza emulator is only available for windows installation. 
It consists of 2 VMware images which theoretically could be run on any VMware environment, but this is not tested.

1. Install the Netezza Emulator from IBM Netezza Developer Network
   * Download [VMware](https://my.vmware.com/web/vmware/downloads) Player.
   * Download the [Netezza Emulator](https://www14.software.ibm.com/webapp/iwm/web/reg/pick.do?source=swg-im-ibmndn&lang=en_US)
   * Also download the Netezza Client components from the same location. This contains the JDBC driver and the IBM netezza Administrator GUI
  
     **NOTE:** you will need to register and create an IBM developer network account
     
   * Install VWware 
   * Verify the Netezza Emulator requirements and install with the installer
     **NOTE:** The Netezza Emulator typically requires >= 4G of ram

   * Install the JDBC driver. This creates a seperate directory which contains the nzjdbc.jar needed.
   * Optionally install the Netezza Administrator GUI (makes life a lot easier)
     **NOTE:** the default passwords for the emulator are nz/nz for the linux host and admin/password for the database connection
  
2. Setup a Development environment  
   Unfortunately gradlew doesn't play nice with windows for test. 
   It is therefore more convenient to set up a seperate VWware linux image on the same virtual network as the emulator. 
   **NOTE:** this is tested with ubuntu 14.04 LTS with git installed.

   * Download and install an ubuntu ISO and create a development VM in the same VMware environment as the Netezza emulator.
   * Make sure GIT is installed and you have a working internet connection from the VM
   * Test if the Netezza Emulator is visible from the development vm (example: $ping 192.168.85.128)
   * Optionally install the linux client tools on the VM.

3. Create the database NETEZZATEST using either the Administrator GUI or nzsql 

4. Run 001_add_objects_needed_for_acceptance_test.sql to create the objects needed for the coretests

5. Rename the nzjdbc.jar to nzjdbc-7.1.jar and put it in the custom libs

6. Build dbfit excluding tests (gradlew clean check install -x test)

7. Run the tests ./gradlew :dbfit-java:netezza:test