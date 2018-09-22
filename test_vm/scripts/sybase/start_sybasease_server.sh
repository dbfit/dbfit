#!/usr/bin/env bash
# Run as root to set up DbFit database.
# Exit statuses: -
# 0 - completed successfully.
# 1 - error occured.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

SYBASE_SCRIPTS=`dirname $0`

# Set Sybase environment.
. /opt/sap/SYBASE.sh

export PATH=$PATH:$SYBASE/$SYBASE_ASE/bin
cd $SYBASE/$SYBASE_ASE/install
if [ $? -ne 0 ]
then
    echo "$EM cannot change to ASE install directory"
    exit 1
fi

# Start the server.
startserver -f RUN_DBFITVM
if [ $? -ne 0 ]
then
    echo "$EM starting DbFit ASE server"
    exit 1
fi

exit 0
