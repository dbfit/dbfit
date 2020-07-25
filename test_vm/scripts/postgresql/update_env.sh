#!/usr/bin/env bash

PG_VERSION=9.6
PG_CONF="/var/lib/pgsql/$PG_VERSION/data/postgresql.conf"
PG_HBA="/var/lib/pgsql/$PG_VERSION/data/pg_hba.conf"
PG_DIR="/var/lib/pgsql/$PG_VERSION/data"

PROVISIONED_ON=/var/lib/pgsql/vm_provision_on_postgres_timestamp

if [ -f "$PROVISIONED_ON" ]
then
  echo "VM was already provisioned at: $(cat $PROVISIONED_ON)"
  echo "To run system updates manually login via 'vagrant ssh' and run 'yum -y update && yum -y upgrade'"
  echo ""
  print_db_usage
  exit
fi

yum install -y sed

# Edit postgresql.conf to change listen address to '*'
sed -i "/listen_addresses = /clisten_addresses = '*'" "$PG_CONF"

# Append to pg_hba.conf to add password auth
echo "host    all             all             all                     md5" >> "$PG_HBA"

# Explicitly set default client_encoding
echo "client_encoding = utf8" >> "$PG_CONF"

date > "$PROVISIONED_ON"