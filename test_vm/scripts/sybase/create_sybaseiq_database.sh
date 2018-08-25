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
SYBASE_DB_DIR=/var/sybaseiq_db

# Set Sybase IQ environment.
. /opt/sap/IQ.sh

# Start a server with no databases.
start_iq -n dbfit -su sa,DbFit1
if [ $? -ne 0 ]
then
    echo "$EM starting basic server"
    exit 1
fi

# Create database directory.
if [ ! -d ${SYBASE_DB_DIR} ]
then
    echo "$IM creating Sybase IQ database directory"
    mkdir ${SYBASE_DB_DIR}
    if [ $? -ne 0 ]
    then
        echo "$EM creating Sybase IQ database directory: ${SYBASE_DB_DIR}"
        exit 1
    fi
fi

# Connect to basic server's utility database to create dbfit database.
dbisql -nogui -c "uid=sa;pwd=DbFit1;eng=dbfit;links=tcpip;dbn=utility_db" \
	$SYBASE_SCRIPTS/create_sybaseiq_database.sql
if [ $? -ne 0 ]
then
    echo "$EM starting basic server"
    exit 1
fi

# Stop the basic server.
dbstop dbfit -c "uid=sa;pwd=DbFit1;eng=dbfit;dbn=utility_db"
if [ $? -ne 0 ]
then
    echo "$EM stopping basic server"
    exit 1
fi

$SYBASE_SCRIPTS/start_sybaseiq_database.sh
if [ $? -ne 0 ]
then
    echo "$EM starting dbfit database server"
    exit 1
fi

dbisql -nogui -c "uid=sa;pwd=DbFit1;eng=dbfit;links=tcpip;dbn=dbfit" \
	$SYBASE_SCRIPTS/create_sybaseiq_objects.sql
if [ $? -ne 0 ]
then
    echo "$EM creating database objects"
    exit 1
fi

exit 0
