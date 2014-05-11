#!/usr/bin/env bash
# Run as root to detect the presence of an existing db2inst1 DB2 instance.
# Exit statuses: -
# 0 - instance detected.
# 1 - error occured.
# 2 - instance not detected.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

# Check for an existing installation of the instance that we install.
echo "$IM checking for presence of existing 'db2inst1' DB2 instance..."
DB2ILISTOUT=/tmp/$MODNAME.db2list.$$

grep -q db2inst1 /etc/passwd
XS=$?
if [ $XS -eq 0 ]
then
	echo "$IM found existing 'db2inst1' instance owner OS user"
	
	echo "$IM checking for presence of 'db2inst1' instance..."
	runuser -l db2inst1 -c "db2ilist > $DB2ILISTOUT"
	if [ $? -ne 0 ]
	then
		echo "$EM executing 'db2ilist' utility" >&2
		exit 1
	fi
	
	grep -q db2inst1 $DB2ILISTOUT
	XS=$?
	if [ $XS -ge 2 ]
	then
		echo "$EM examining output of 'db2ilist' utility" >&2
		exit 1
	elif [ $XS -eq 1 ]
	then
		echo "$IM 'db2inst1' instance not detected"
		exit 2
	else
		echo "$IM detected existing 'db2inst1' instance"
	fi
elif [ $XS -ge 2 ]
then
	echo "$EM detecting presence of 'db2inst1' instance owner OS user" >&2
	exit 1
else
	echo "$IM instance owner OS user 'db2inst1' not detected"
	exit 2
fi

# We could go on to check for the presence of dasusr1 and db2fenc1 OS users,
# and may be the DBFIT database and DFTEST schema and DFTEST OS user, etc, etc...
exit 0
