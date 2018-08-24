#!/usr/bin/env bash
# Run as root to install Sybase IQ and set up DbFit database.
# Exit statuses: -
# 0 - installed successfully.
# 1 - error occured.
# 2 - Sybase IQ instance already installed - nothing to do.
# 3 - Sybase IQ instance not installed - no installer found.
MODNAME=`basename $0`
IM="$MODNAME: INFO:"
EM="$MODNAME: ERROR:"
WM="$MODNAME: WARNING:"

SYBASE_INST_BASE=/tmp
SYBASE_UNTAR=sybaseiq
SYBASE_INST_ROOT=${SYBASE_INST_BASE}/${SYBASE_UNTAR}
SYBASE_SCRIPTS=`dirname $0`
DBFIT_ROOT=/var/dbfit

DOLIBS=true
DODETECT=true
DOFORCE=

while getopts "dfl" OPT
do
    case $OPT in
    d)  DODETECT=
        ;;
    l)  DOLIBS=
        ;;
    f)  DOFORCE=true
        ;;
    \?) echo "$EM invalid option: $OPTARG" 1>&2
        exit 1
        ;;
    esac
done

# Check for an existing installation of the instance that we install.
if [ "$DODETECT" ]
then
    ${SYBASE_SCRIPTS}/detect_sybaseiq.sh
    XS=$?
    if [ $XS -eq 0 ]
    then
        echo "$IM existing Sybase IQ instance detected"
        if [ "$DOFORCE" ]
        then
            echo "$IM force installation requested"
        else
            echo "$IM force-install not requested. Nothing to do. Exiting..."
            exit 2
        fi
    elif [ $XS -eq 2 ]
    then
        echo "$IM no existing Sybase IQ instance detected"
    else
        echo "$EM attempting to detect presence of Sybase IQ instance" 1>&2
        exit 1
    fi
fi

if [ "$DOLIBS" ]
then
    echo "$IM installing dependency library packages..."
    yum -y install csh
    if [ $? -ne 0 ]
    then
        echo "$EM installing csh" 1>&2
        exit 1
    fi
fi

cd "${SYBASE_INST_BASE}"
if [ $? -ne 0 ]
then
    echo "$EM cannot change to ${SYBASE_INST_BASE} directory" 1>&2
    exit 1
fi

DOSETUP=

# Check for presence of unzipped TAR installation package.
if [ -d ${SYBASE_INST_ROOT} ]
then
    DOSETUP=true
    echo "$IM found installation directory"
# Check for presence of zipped tarball installation package.
# NOTE: the zipped tarball must have been given the fixed name "Linux64-iq.tgz".
elif [ -f ${DBFIT_ROOT}/Linux64-iq.tgz ]
then
    DOSETUP=true
    echo "$IM found gzipped installation tarball. Unpacking..."
    mkdir ${SYBASE_INST_ROOT}
    cd ${SYBASE_INST_ROOT}
    tar --no-same-owner -zxf ${DBFIT_ROOT}/Linux64-iq.tgz
    if [ $? -ne 0 ]
    then
        echo "$EM unpacking gzipped installation tarball" 1>&2
        exit 1
    fi
    cd $OLDPWD
fi

if [ ! "$DOSETUP" ]
then
    echo "$IM no Sybase IQ installer package files found. Exiting..."
    exit 3
fi

cd ${SYBASE_INST_ROOT}/Linux64-iq*
if [ $? -ne 0 ]
then
    echo "$EM cannot change to installer binary directory" 1>&2
    exit 1
fi

# Install using a response file.
echo "$IM running setup.bin utility..."
${PWD}/setup.bin -i silent -DAGREE_TO_SAP_LICENSE=true -f ${DBFIT_ROOT}/test_vm/scripts/sybase/sybaseiq_typical.rsp
if [ $? -ne 0 ]
then
    echo "$EM executing setup.bin utility" 1>&2
    exit 1
fi

$SYBASE_SCRIPTS/create_sybaseiq_database.sh
if [ $? -ne 0 ]
then
    echo "$EM creating Sybase IQ database" 1>&2
    exit 1
fi

echo "$IM removing Sybase IQ installer package files..."
rm -fr ${SYBASE_INST_ROOT} 2>/dev/null
if [ $? -ne 0 ]
then
    echo "$WM cannot remove Sybase IQ installer packages files"
fi

exit 0
