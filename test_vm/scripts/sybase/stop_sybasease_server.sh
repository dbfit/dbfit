. /opt/sap/SYBASE.sh
export PATH=$PATH:$SYBASE/$SYBASE_OCS
isql -Usa -Pdbfitvm -SDBFITVM -i sql/stop-server-sybasease.sql
