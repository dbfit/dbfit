#!/usr/bin/env bash

THIS_DIR="`dirname $0`" && cd "${THIS_DIR}" && THIS_DIR="`pwd`" || { "Can't change to ${THIS_DIR}!"; exit 1; }

psql -c 'create database dbfit;' -U postgres &&
psql -c "create user dbfit with password 'dbfit';" -U postgres &&
psql -c "grant all privileges on database dbfit to dbfit;" -U postgres &&
psql -d dbfit -U postgres -f "${THIS_DIR}/sql/create-db-schema-postgresql.sql"
