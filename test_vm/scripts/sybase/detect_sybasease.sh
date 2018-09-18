#!/usr/bin/env bash
# Run as root to detect the presence of an existing Sybase ASE instance.
# Exit statuses: -
# 0 - instance detected.
# 1 - error occured.
# 2 - instance not detected.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

# Check for an existing installation of the instance that we install.
echo "$IM checking for presence of existing Sybase ASE..."
SYBASELISTOUT=/tmp/$MODNAME.sybaselist.$$

if [ $(find /opt/sap -name 'dataserver' 2>/dev/null | wc -l) -gt 0 ]
then
	echo "$IM found existing Sybase ASE binaries"
else
	echo "$IM existing Sybase ASE binaries not found"
	exit 2
fi

exit 0
