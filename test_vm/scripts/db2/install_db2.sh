#!/usr/bin/env bash
# Run as root to install DB2 and set up DbFit database.
# Exit statuses: -
# 0 - installed successfully.
# 1 - error occured.
# 2 - DB2 instance already installed - nothing to do.
# 3 - DB2 instance not installed - no installer found.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

DB2_INST_ROOT=/tmp
DB2_SCRIPTS=`dirname $0`
DBFIT_ROOT=/var/dbfit

DOLIBS=true
DODETECT=true
DOFORCE=

while getopts "dfl" OPT
do
	case $OPT in
	d)	DODETECT=
		;;
	l)	DOLIBS=
		;;
	f)	DOFORCE=true
		;;
	\?)	echo "$EM invalid option: $OPTARG" 1>&2
		exit 1
		;;
	esac
done

# Check for an existing installation of the instance that we install.
if [ "$DODETECT" ]
then
	$DB2_SCRIPTS/detect_db2.sh
	XS=$?
	if [ $XS -eq 0 ]
	then
		echo "$IM existing 'db2inst1' instance detected"
		if [ "$DOFORCE" ]
		then
			echo "$IM force installation requested"
		else
			echo "$IM force installation not requested. Nothing to do. Exiting..."
			exit 2
		fi
	elif [ $XS -eq 2 ]
	then
		echo "$IM no existing 'db2inst1' instance detected"
	else
		echo "$EM attempting to detect presence of existing 'db2inst1' instance" 1>&2
		exit 1
	fi
fi

if [ "$DOLIBS" ]
then
	echo "$IM installing library packages..."
	
	yum -y install libstdc++.x86_64 libstdc++.i686
	if [ $? -ne 0 ]
	then
		echo "$EM installing libstdc libraries" 1>&2
		exit 1
	fi

	yum -y install pam-devel.x86_64 pam-devel.i686
	if [ $? -ne 0 ]
	then
		echo "$EM installing pam libraries" 1>&2
		exit 1
	fi

	# IBM docs (for v9.5 at least) say that db2setup needs 32 and 64-bit libaio.
	# See: http://publib.boulder.ibm.com/infocenter/db2luw/v9r5/index.jsp?topic=%2Fcom.ibm.db2.luw.qb.server.doc%2Fdoc%2Fr0008865.html
	# This appears to be untrue by v10.5. We install 64-bit here.
	yum -y install libaio
	if [ $? -ne 0 ]
	then
		echo "$EM installing async IO libraries" 1>&2
		exit 1
	fi
	
	yum -y install numactl
	if [ $? -ne 0 ]
	then
		echo "$EM installing tuning for Non Uniform Memory Access machines libraries" 1>&2
		exit 1
	fi
fi

cd "${DB2_INST_ROOT}"
if [ $? -ne 0 ]
then
	echo "$EM cannot change to ${DB2_INST_ROOT} directory" 1>&2
	exit 1
fi

DOSETUP=
DORPM=

# Check for presence of unzipped TAR installation package.
if [ -d expc ]
then
	DOSETUP=true
	echo "$IM found installation directory"
# Check for presence of zipped tarball installation package.
# NOTE: the zipped tarball must have been renamed to the fixed name "db2_linuxx64_expc.tar.gz".
# Download here: https://www-01.ibm.com/marketing/iwm/mrs/?source=swg-db2expressc (last check 20200723)
# Last test with version v11.1_linuxx64_expc.tar.gz
elif [ -f ${DBFIT_ROOT}/db2_linuxx64_expc.tar.gz ]
then
	DOSETUP=true
	echo "$IM found gzipped installation tarball. Unpacking..."
	tar --no-same-owner -zxf ${DBFIT_ROOT}/db2_linuxx64_expc.tar.gz -C ${DB2_INST_ROOT}
	if [ $? -ne 0 ]
	then
		echo "$EM unpacking gzipped installation tarball" 1>&2
		exit 1
	fi
fi

if [ "$DOSETUP" ]
then
	cd expc
	if [ $? -ne 0 ]
	then
		echo "$EM cannot change to ${DB2_INST_ROOT}/expc directory" 1>&2
		exit 1
	fi
	# Install DB2 using a response file.
	echo "$IM running db2setup utility..."
	./db2setup -r ${DBFIT_ROOT}/test_vm/scripts/db2/db2expc_typical.rsp
	if [ $? -ne 0 ]
	then
		echo "$EM executing db2setup utility" 1>&2
		exit 1
	fi
fi

# if [ -f IBM-DB2-EXPC-latest.x86_64.rpm ]
# then
	# echo "$IM: found RPM file. Installing..."
	# DORPM=true
	# yum -y install IBM-DB2-EXPC-latest.x86_64.rpm
	# if [ $? -ne 0 ]
	# then
		# echo "$EM: installing from RPM package" 1>&2
		# exit 1
	# fi	
# fi

if [ ! "$DOSETUP" -a ! "$DORPM" ]
then
	echo "$IM no DB2 installer package files found. Exiting..."
	exit 3
fi

$DB2_SCRIPTS/create_db_schema.sh
if [ $? -ne 0 ]
then
	echo "$EM creating DB2 'DBFIT' DB and 'DFTEST' schema" 1>&2
	exit 1
fi

echo "$IM removing DB2 installer package files..."
rm -fr "$DB2_INST_ROOT/expc" 2>/dev/null
if [ $? -ne 0 ]
then
	echo "$WM cannot remove DB2 installer packages files"
fi

exit 0
