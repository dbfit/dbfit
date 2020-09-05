#!/usr/bin/env bash
# Run as root to deinstall Sybase IQ.
# Exit statuses: -
# 0 - deinstalled successfully.
# 1 - error occured.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

SYBASEIQBASEDIR=/opt/sap/sybuninstall/ASESuite

${SYBASEIQBASEDIR}/uninstall -i silent
if [ $? -ne 0 ]
then
	echo "$EM deinstalling Sybase ASE components" >&2
	exit 1
fi

exit 0
