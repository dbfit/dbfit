#!/bin/sh
# Check the presence of a specific number of database manager start-up messages.
if [ "$#" -ne 1 ]
then
    echo Usage: $0 number-of-dbstart-messages-for-which-to-check >&2
    exit 1
fi

COUNT=`grep "MESSAGE : ADM7513W  Database manager has started." /home/db2inst1/sqllib/db2dump/db2diag.log | wc -l` >/dev/null

echo db2diag.log contains $COUNT database manager startup messages
if [ "$COUNT" -eq "$1" ]
then
    exit 0
else
    exit 1
fi
