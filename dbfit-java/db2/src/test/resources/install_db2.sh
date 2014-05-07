#!/usr/bin/env bash
# Run as root to install DB2 and set up DbFit database.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"

DB2_INST_ROOT=/var/dbfit
DB2_SQL_SCRIPTS=/var/dbfit/dbfit-java/db2/src/test/resources

yum -y install libaio
if [ $? -ne 0 ]
then
	echo "$EM installing 32-bit async IO libraries" 1>&2
	exit 1
fi

yum -y install pam.i686
if [ $? -ne 0 ]
then
	echo "$EM installing Pluggable Authentication Module libraries" 1>&2
	exit 1
fi

yum -y install numactl-2.0.7-8.el6.x86_64
if [ $? -ne 0 ]
then
	echo "$EM installing tuning for Non Uniform Memory Access machines libraries" 1>&2
	exit 1
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
elif [ -f db2_linuxx64_expc.tar.gz ]
then
	DOSETUP=true
	echo "$IM found gzipped installation tarball. Unpacking..."
	tar zxvf db2_linuxx64_expc.tar.gz >/dev/null
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
	./db2setup -r ${DB2_INST_ROOT}/dbfit-java/db2/src/test/resources/db2expc_typical.rsp
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
	exit 0
fi

# Set DB2 environment.
DB2PROFILE=/home/db2inst1/sqllib/db2profile

# Create dftest OS user for dbfit tests.
echo "$IM creating OS user 'dftest'..."
useradd dftest -p DFTEST
if [ $? -ne 0 ]
then
	echo "$EM creating 'dftest' OS user" 1>&2
	exit 1
fi

# Create dftest db and dftest schema.
# Run as db2inst1 instance owner.
echo "$IM creating 'DBFIT' database and 'DFTEST' schema..."
runuser -l db2inst1 -c "db2 -vf '$DB2_SQL_SCRIPTS/create-db-schema-db2.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating DB2 'DBFIT' DB and 'DFTEST' schema" 1>&2
	exit 1
fi

echo "INFO: creating DbFit acceptance testing DB objects..."

runuser -l dftest -c ". '$DB2PROFILE' && db2 -vf '$DB2_SQL_SCRIPTS/acceptancetests-db2.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating DB2 acceptance testing DB objects" 1>&2
	exit 1
fi

DBFITLIBDIR=/var/dbfit/dist/lib

if [ ! -d "$DBFITLIBDIR" ]
then
	mkdir "$DBFITLIBDIR"
	if [ $? -ne 0 ]
	then
		echo "$EM creating DbFit lib directory $DBFITLIBDIR" 1>&2
		exit 1
	fi
fi

DB2JDBCDRIVER=/opt/ibm/db2/V10.5/java/db2jcc4.jar
cp "$DB2JDBCDRIVER" "$DBFITLIBDIR"
if [ $? -ne 0 ]
then
	echo "$EM copying DB2 JDBC driver $DB2JDBCDRIVER to DbFit lib directory $DBFITLIBDIR" 1>&2
	exit 1
fi

echo "$IM removing DB2 installer package files..."
rm -fe "$DB2_INST_ROOT/expc"

exit 0
