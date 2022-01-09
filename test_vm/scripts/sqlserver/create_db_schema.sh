#!/usr/bin/env bash

set -ev
env

THIS_DIR="`dirname $0`" && cd "${THIS_DIR}" && THIS_DIR="`pwd`" || { "Can't change to ${THIS_DIR}!"; exit 1; }

sql_dir="./sql"
sql_cmd="/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P ${SA_PASSWORD}"

${sql_cmd} -i ${sql_dir}/create-databases-sqlserver.sql
${sql_cmd} -i ${sql_dir}/create-dbdeploy-changelog-sqlserver.sql
