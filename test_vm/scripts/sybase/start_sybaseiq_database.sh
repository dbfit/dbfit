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

# Set Sybase IQ environment.
. /opt/sap/IQ.sh

# Start a server with no databases.
start_iq -n dbfit /var/dbfit/sybaseiq_db/dbfit.db
if [ $? -ne 0 ]
then
    echo "$EM starting dbfit database server"
    exit 1
fi

exit 0
