remote_file '/etc/yum.repos.d/mssql-server.repo' do
  source 'https://packages.microsoft.com/config/rhel/7/mssql-server-2017.repo'
  action :create
end

# not working anymore, gives no candidate version to install. We will install directly.
#package 'mssql-server'
execute 'configure SQL Server' do
  command 'yum install -y mssql-server'
  action :run
  not_if 'systemctl status mssql-server | grep active (running)'
end

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

# not working anymore, gives no candidate version to install. We will install directly.
#package 'mssql-tools'
execute 'configure SQL Server' do
  command 'yum install -y mssql-tools'
  action :run
end

sql_dir = File.join(node['dbfit']['project_root'], "test_vm/scripts/sqlserver/sql")
sql_acceptance_dir = File.join(node['dbfit']['project_root'], "dbfit-java/sqlserver/src/integration-test/resources")
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

detect_acceptancedb = "#{sql_cmd} -Q 'SELECT COUNT(*) FROM dbfit.dbo.users'"

execute 'prepare database for acceptance testing' do
  command "#{sql_cmd} -i #{sql_acceptance_dir}/001_create_acceptance_test_objects_sqlserver.sql"
  action :run
  not_if "#{detect_acceptancedb}"
end
