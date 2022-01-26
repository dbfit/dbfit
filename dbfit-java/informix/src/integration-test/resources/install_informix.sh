#!/usr/bin/env bash
# Run as root to install Informix and set up DbFit database.
# Exit statuses: -
# 0 - installed successfully.
# 1 - error occured.
# 2 - DB2 instance already installed - nothing to do.
# 3 - DB2 instance not installed - no installer found.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

DBFIT_ROOT=/var/dbfit
INFORMIX_INST_ROOT=/tmp
INFORMIX_INST_TARG_ROOT=/informix
INFORMIX_SCRIPTS=$DBFIT_ROOT/dbfit-java/informix/src/integration-test/resources
INFORMIX_TAR=ids-linux-x86_64.tar

DODETECT=true
DOFORCE=

while getopts "df" OPT
do
	case $OPT in
	d)	DODETECT=
		;;
	f)	DOFORCE=true
		;;
	\?)	echo "$EM invalid option: $OPTARG" 1>&2
		exit 1
		;;
	esac
done

# Check for an existing installation.
if [ "$DODETECT" ]
then
	$INFORMIX_SCRIPTS/detect_informix.sh
	XS=$?
	if [ $XS -eq 0 ]
	then
		echo "$IM existing Informix installation detected"
		if [ "$DOFORCE" ]
		then
			echo "$IM force installation requested"
		else
			echo "$IM force installation not requested. Nothing to do. Exiting..."
			exit 2
		fi
	elif [ $XS -eq 2 ]
	then
		echo "$IM no existing Informix installation detected"
	else
		echo "$EM attempting to detect presence of existing Informix installation" 1>&2
		exit 1
	fi
fi

cd "${INFORMIX_INST_ROOT}"
if [ $? -ne 0 ]
then
	echo "$EM cannot change to ${INFORMIX_INST_ROOT} directory" 1>&2
	exit 1
fi

DOSETUP=

# Check for presence of TAR installation package.
if [ -d ifxunpacked ]
then
	DOSETUP=true
	echo "$IM found installer directory"
# Check for presence of tarball installation package.
# NOTE: the tarball must have been renamed to the fixed name.
elif [ -f ${DBFIT_ROOT}/$INFORMIX_TAR ]
then
	DOSETUP=true
	echo "$IM found installation tarball. Unpacking..."
	mkdir ifxunpacked
	if [ $? -ne 0 ]
	then
		echo "$EM creating directory ifxunpacked" 1>&2
		exit 1
	fi
	cd ifxunpacked
	if [ $? -ne 0 ]
	then
		echo "$EM changing working directory to ifxunpacked" 1>&2
		exit 1
	fi
	tar xf ${DBFIT_ROOT}/$INFORMIX_TAR
	if [ $? -ne 0 ]
	then
		echo "$EM unpacking installation tarball" 1>&2
		exit 1
	fi
	cd "${INFORMIX_INST_ROOT}"
	if [ $? -ne 0 ]
	then
		echo "$EM cannot change to ${INFORMIX_INST_ROOT} directory" 1>&2
		exit 1
	fi
else
	echo "$IM no installation directory or tarball found"
fi

if [ "$DOSETUP" ]
then
	mkdir $INFORMIX_INST_TARG_ROOT
	if [ $? -ne 0 ]
	then
		echo "$EM cannot create base installation directory $INFORMIX_INST_TARG_ROOT" 1>&2
		exit 1
	fi
	mkdir $INFORMIX_INST_TARG_ROOT/storage
	if [ $? -ne 0 ]
	then
		echo "$EM cannot create base storage directory $INFORMIX_INST_TARG_ROOT/storage" 1>&2
		exit 1
	fi
	cd ifxunpacked
	if [ $? -ne 0 ]
	then
		echo "$EM cannot change to ${INFORMIX_INST_ROOT}/ifxunpacked directory" 1>&2
		exit 1
	fi
	# Install Informix using a response file.
	echo "$IM running ids_install utility..."
	if [ "$DOFORCE" ]
	then
		FORCEOPTION=-DOVERWRITE_PRODUCT=TRUE
	else
		FORCEOPTION=
	fi
	./ids_install -i silent -f ${DBFIT_ROOT}/dbfit-java/informix/src/integration-test/resources/informix_install_custom.properties $FORCEOPTION -DLOG_FILE=$INFORMIX_INST_ROOT/ids_install.log -DDEBUG_LEVEL=1 -DDEBUG_FILE=$INFORMIX_INST_ROOT/ids_install_debug.log
	if [ $? -ne 0 ]
	then
		echo "$EM executing ids_install utility" 1>&2
		exit 1
	fi
else
	echo "$IM no Informix installer package files found. Exiting..."
	exit 3
fi

echo "$IM removing Informix installer package files..."
cd $INFORMIX_INST_TARG_ROOT
if [ $? -ne 0 ]
then
	echo "$EM cannot change working directory to Informix installation directory $INFORMIX_INST_TARG_ROOT" 1>&2
	exit 1
fi

rm -fr "$INFORMIX_INST_ROOT/ifxunpacked" 2>/dev/null
if [ $? -ne 0 ]
then
	echo "$WM cannot remove Informix installer packages files"
fi

# Create dbfit OS user for dbfit tests.
echo "$IM creating OS user 'dbfit'..."
useradd dbfit
if [ $? -ne 0 ]
then
	echo "$EM creating 'dbfit' OS user" 1>&2
	exit 1
fi

echo "$IM setting passwd for OS user 'dbfit'..."
echo dbfit:DBFIT|chpasswd
if [ $? -ne 0 ]
then
	echo "$EM setting passwd for OS user 'dbfit'" 1>&2
	exit 1
fi

echo "$IM setting environment for dbacccess..."
. $INFORMIX_INST_TARG_ROOT/dbfitifserver.ksh
if [ $? -ne 0 ]
then
	echo "$EM setting environment for dbacccess" 1>&2
	exit 1
fi

# Disable IPV6 for client connections (requires the instance to be restarted).
touch $INFORMIX_INST_TARG_ROOT/etc/IFX_DISABLE_IPV6
if [ $? -ne 0 ]
then
	echo "$EM cannot create file $INFORMIX_INST_TARG_ROOT/etc/IFX_DISABLE_IPV6" 1>&2
	exit 1
fi

chown informix:informix $INFORMIX_INST_TARG_ROOT/etc/IFX_DISABLE_IPV6
if [ $? -ne 0 ]
then
	echo "$EM cannot change ownership of file $INFORMIX_INST_TARG_ROOT/etc/IFX_DISABLE_IPV6" 1>&2
	exit 1
fi

# Restart the Informix instance.
echo "$IM stopping Informix instance..."
runuser informix -c "onmode -ky"
if [ $? -ne 0 ]
then
	echo "$EM stopping Informix instance"
	exit 1
fi

echo "$IM starting Informix instance..."
runuser informix -c "oninit -vwy"
if [ $? -ne 0 ]
then
	echo "$EM starting Informix instance"
	exit 1
fi

echo "$IM creating 'DBFIT' database..."
runuser dbfit -c "dbaccess sysmaster '$INFORMIX_SCRIPTS/create-db-informix.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating Informix 'DBFIT' DB" 1>&2
	exit 1
fi

echo "$IM creating DbFit acceptance testing DB objects in 'dbfit_ansi' database..."
runuser dbfit -c "dbaccess dbfit_ansi '$INFORMIX_SCRIPTS/create-acceptance-test-objects-informix.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating acceptance testing DB objects in 'dbfit_ansi' database" 1>&2
	exit 1
fi

echo "$IM creating DbFit acceptance testing DB objects in 'dbfit_ifx' database..."
runuser dbfit -c "dbaccess dbfit_ifx '$INFORMIX_SCRIPTS/create-acceptance-test-objects-informix.sql'"
if [ $? -ne 0 ]
then
	echo "$EM creating acceptance testing DB objects in 'dbfit_ifx' database" 1>&2
	exit 1
fi
exit
