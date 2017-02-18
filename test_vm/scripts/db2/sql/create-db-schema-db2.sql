create database DBFIT
connect to DBFIT
create schema DFTEST
grant connect on database to user dftest
grant createtab on database to user dftest
grant alterin, createin, dropin on schema dftest to user dftest
grant dataaccess on database to user dftest
