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

DBISQL_EXE=$(find /opt/sap -name dbisql | grep DBISQL-.*/bin | head -1)
if [ "$DBISQL_EXE" = "" ]
then
    echo "$EM cannot find dbisql binary"
    exit 1
fi

DBISQL_BIN=$(dirname "$DBISQL_EXE")
export PATH=$PATH:$DBISQL_BIN

# Create dbfit database.
dbisql -nogui -host localhost -port 5000 -c "UID=sa;PWD=dbfitvm"
if [ $? -ne 0 ]
then
    echo "$EM starting dbisql"
    exit 1
fi

exit 0
