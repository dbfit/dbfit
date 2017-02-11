#!/usr/bin/env bash
# Run as root to set up DbFit database.
# Exit statuses: -
# 0 - completed successfully.
# 1 - error occured.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

DBFIT_ROOT=/var/dbfit
DB2_SCRIPTS=$DBFIT_ROOT/dbfit-java/db2/src/integration-test/resources

# Set DB2 environment.
DB2PROFILE=/home/db2inst1/sqllib/db2profile

# Create dftest OS user for dbfit tests.
echo "$IM creating OS user 'dftest'..."
useradd dftest
if [ $? -ne 0 ]
then
	echo "$EM creating 'dftest' OS user" 1>&2
	exit 1
fi

echo "$IM setting passwd for OS user 'dftest'..."
echo dftest:DFTEST|chpasswd
if [ $? -ne 0 ]
then
	echo "$EM setting passwd for OS user 'dftest'" 1>&2
	exit 1
fi

# Create dftest DB and dftest schema.
# Run as db2inst1 instance owner.
echo "$IM creating 'DBFIT' database and 'DFTEST' schema..."
runuser -l db2inst1 -c "db2 -vf '$DB2_SCRIPTS/create-db-schema-db2.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating DB2 'DBFIT' DB and 'DFTEST' schema" 1>&2
	exit 1
fi

echo "INFO: creating DbFit acceptance testing DB objects..."
runuser -l dftest -c ". '$DB2PROFILE' && db2 -vf '$DB2_SCRIPTS/acceptancetests-db2.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating DB2 acceptance testing DB objects" 1>&2
	exit 1
fi

exit 0
