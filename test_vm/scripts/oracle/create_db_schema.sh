set -ev

env
lsnrctl services

sqlplus ${ORACLE_ADMIN_USER}/${ORACLE_ADMIN_PASSWORD}@${ORACLE_URL} <<SQL
@@ sql/acceptancescripts-Oracle.sql
SQL
