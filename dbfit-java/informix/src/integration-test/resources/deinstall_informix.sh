#!/usr/bin/env bash
# Run as root to deinstall Informix and any existing database server.
# Exit statuses: -
# 0 - deinstalled successfully.
# 1 - error occured.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

INFORMIXDIR=/informix
INFORMIX_SCRIPTS=$DBFIT_ROOT/dbfit-java/informix/src/integration-test/resources

if [ ! -x "$INFORMIXDIR/uninstall/uninstall_ids" ]
then
	echo "$EM cannot find Informix deinstaller $INFORMIXDIR/uninstall/uninstall_ids" >&2
	exit 1
fi

$INFORMIXDIR/uninstall/uninstall_server/uninstallserver -i silent -f $INFORMIX_SCRIPTS/informix_deinstall.properties
if [ $? -ne 0 ]
then
	echo "$EM running Informix deinstaller" >&2
	exit 1
fi

userdel -fr informix
if [ $? -ne 0 ]
then
	echo "$EM dropping informix OS user" >&2
	exit 1
fi

groupdel informix
if [ $? -ne 0 ]
then
	echo "$EM dropping informix OS group" >&2
	exit 1
fi

grep -v "^informix_dbfit " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing Informix services from /etc/services" >&2
	exit 1
fi

rm -fr /informix
if [ $? -ne 0 ]
then
	echo "$EM removing Informix installation directory tree /informix" >&2
	exit 1
fi

exit 0
