#!/usr/bin/env bash
# Run as root to detect the presence of an existing Informix installation.
# Exit statuses: -
# 0 - installation detected.
# 1 - error occured.
# 2 - installation not detected.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

INFORMIX_INST_TARG_ROOT=/informix

# Check for an existing installation.
echo "$IM checking for presence of existing Informix installation..."

# Check for the informix user.
grep -q informix /etc/passwd
XS=$?
if [ $XS -eq 0 ]
then
	echo "$IM found existing 'informix' OS user"
elif [ $XS -ge 2 ]
then
	echo "$EM detecting presence of informix OS user" >&2
	exit 1
else
	echo "$IM informix OS user not detected"
	exit 2
fi

# Check for the Informix installation directory.
echo "$IM checking for presence of installation directory..."
if [ ! -d $INFORMIX_INST_TARG_ROOT ]
then
	echo "$IM installation directory $INFORMIX_INST_TARG_ROOT not found"
	exit 2
else
	echo "$IM installation directory $INFORMIX_INST_TARG_ROOT found"
fi

# Check for the DBFIT database.
. $INFORMIX_INST_TARG_ROOT/dbfitserver.ksh
echo SELECT name FROM sysmaster:sysdatabases | $INFORMIX_INST_TARG_ROOT/bin/dbaccess sysmaster - 2>/dev/null | grep dbfit >/dev/null 2>&1
if [ $? -ne 0 ]
then
	echo "$IM database 'dbfit' not detected"
	exit 2
else
	echo "$IM database 'dbfit' detected"
fi

exit 0
