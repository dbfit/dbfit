#!/bin/sh
RPM_PATH=/var/dbfit/
ORACLE_ENV_SCRIPT=/u01/app/oracle/product/11.2.0/xe/bin/oracle_env.sh
ORACLE_SQL_SCRIPT=/var/dbfit/dbfit-java/oracle/src/test/resources/acceptancescripts-Oracle.sql 
ORACLE_PW=oracle
ORACLE_ZIP=$(ls $RPM_PATH/oracle-xe-*.rpm.zip)

if [[ -f $ORACLE_ZIP ]]; then
    echo "Oracle rpm found unzipping ..."
    unzip -o $RPM_PATH/oracle-xe-*.rpm.zip
else
    echo 
    echo "Go to http://www.oracle.com/technetwork/products/express-edition/downloads/index.html, download oracle-xe-*.rpm"
    echo "and put it into $RPM_PATH (= root path of dbfit git repo)"
    echo 
    exit 1  
fi

sudo yum install -y $RPM_PATH/Disk1/oracle-xe-*.rpm
if [[ $? != 0 ]]; then
    echo "rpm installation failed"
    exit 1
fi

sudo /etc/init.d/oracle-xe configure<<EOF
8080
1521
$ORACLE_PW
$ORACLE_PW
y
EOF

sudo -u oracle /bin/sh -c ". $ORACLE_ENV_SCRIPT; sqlplus /nolog @$ORACLE_SQL_SCRIPT"

rm -rf $RPM_PATH/Disk1
