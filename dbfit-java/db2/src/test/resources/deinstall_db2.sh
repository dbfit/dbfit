#!/usr/bin/env bash
# Run as root to deinstall DB2 and any existing db2inst1 instance.
# Exit statuses: -
# 0 - deinstalled successfully.
# 1 - error occured.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

DB2BASEDIR=/opt/ibm/db2
DB2VER=`ls $DB2BASEDIR 2>/dev/null | head -1`
if [ ! "$DB2VER" ]
then
	echo "$EM cannot find any DB2 version installations under /opt/ibm/db2" >&2
	exit 1
fi

DB2HOME=$DB2BASEDIR/$DB2VER

runuser -l dasusr1 -c ". ./das/dasprofile && db2admin stop"
if [ $? -ne 0 ]
then
	echo "$EM stopping DB2 admin server" >&2
	exit 1
fi

$DB2HOME/instance/dasdrop
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 admin server" >&2
	exit 1
fi

runuser -l db2inst1 -c "db2stop"
if [ $? -ne 0 ]
then
	echo "$EM stopping DB2 instance" >&2
	exit 1
fi

$DB2HOME/instance/db2idrop_local db2inst1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 instance" >&2
	exit 1
fi

$DB2HOME/install/db2_deinstall -a
if [ $? -ne 0 ]
then
	echo "$EM deinstalling DB2 components" >&2
	exit 1
fi

userdel -fr db2inst1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 instance owner OS user" >&2
	exit 1
fi

userdel -fr dasusr1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 data access server OS user" >&2
	exit 1
fi

userdel -fr db2fenc1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 fenced process OS user" >&2
	exit 1
fi

userdel -fr dftest
if [ $? -ne 0 ]
then
	echo "$EM dropping DbFit 'dftest' user fenced process OS user" >&2
	exit 1
fi

groupdel db2iadm1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 instance admins OS group" >&2
	exit 1
fi

groupdel db2fadm1
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 fenced admins OS group" >&2
	exit 1
fi

groupdel dasadm1 
if [ $? -ne 0 ]
then
	echo "$EM dropping DB2 data access server admins OS group" >&2
	exit 1
fi

grep -v "^db2c_db2inst1 " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing DB2 services from /etc/services" >&2
	exit 1
fi

grep -v "^db2j_db2inst1 " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing DB2 services from /etc/services" >&2
	exit 1
fi

grep -v "^ibm-db2 " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing DB2 services from /etc/services" >&2
	exit 1
fi

grep -v "^questdb2-lnchr " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing DB2 services from /etc/services" >&2
	exit 1
fi

grep -v "^qdb2service " /etc/services > /etc/services
if [ $? -ge 2 ]
then
	echo "$EM removing DB2 services from /etc/services" >&2
	exit 1
fi

rm -fr /var/db2
if [ $? -ne 0 ]
then
	echo "$EM removing DB2 directory /var/db2" >&2
	exit 1
fi

rm -fr /opt/ibm
if [ $? -ne 0 ]
then
	echo "$EM removing DB2 directory /opt/ibm" >&2
	exit 1
fi

exit 0
