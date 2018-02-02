remote_file '/etc/yum.repos.d/mssql-server.repo' do
  source 'https://packages.microsoft.com/config/rhel/7/mssql-server-2017.repo'
  action :create
end

package 'mssql-server'

sa_usr = 'sa'
sa_pwd = 'DbFit000!'
sql_host = 'localhost'

ENV['MSSQL_SA_PASSWORD'] = sa_pwd
ENV['ACCEPT_EULA'] = 'Y'
ENV['MSSQL_PID'] = 'Developer'

service 'stop mssql-server' do
  service_name 'mssql-server'
  action :stop
end

execute 'configure SQL Server' do
  command '/opt/mssql/bin/mssql-conf -n setup'
  action :run
  not_if 'systemctl status mssql-server | grep active (running)'
end

service 'start mssql-server' do
  service_name 'mssql-server'
  action :start
end

remote_file '/etc/yum.repos.d/prod.repo' do
  source 'https://packages.microsoft.com/config/rhel/7/prod.repo'
  action :create
end

package 'mssql-tools'

sql_dir = File.join(node['dbfit']['project_root'], "test_vm/scripts/sqlserver/sql")
sql_bin = '/opt/mssql-tools/bin'
sql_cmd = "#{sql_bin}/sqlcmd -S #{sql_host} -U #{sa_usr} -P #{sa_pwd} -b "

detect_changelog = "#{sql_cmd} -Q 'SELECT COUNT(*) FROM dbfit.dbo.changelog'"

execute 'create databases' do
  command "#{sql_cmd} -i #{sql_dir}/create-databases-sqlserver.sql"
  action :run
  not_if "#{detect_changelog}"
end

execute 'create dbfit.changelog table' do
  command "#{sql_cmd} -i #{sql_dir}/create-dbdeploy-changelog-sqlserver.sql"
  action :run
  not_if "#{detect_changelog}"
end
