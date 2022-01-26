#!/usr/bin/env bash

set -ev
env

THIS_DIR="`dirname $0`" && cd "${THIS_DIR}" && THIS_DIR="`pwd`" || { "Can't change to ${THIS_DIR}!"; exit 1; }

mysql -u root --password="${MYSQL_ROOT_PASSWORD}" -v < "${THIS_DIR}/sql/create-db-mysql.sql"
