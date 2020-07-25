postgresql_connection_info = {:host => "127.0.0.1",
                              :port => 5432,
                              :username => 'postgres',
                              :password => node['postgresql']['password']['postgres']}

sql_dir = File.join(node['dbfit']['project_root'], "test_vm/scripts/postgresql/sql")

postgresql_server_install 'PostgreSQL Server install' do
  action :install
end

postgresql_server_install 'Setup my PostgreSQL server' do
  password node['postgresql']['password']['postgres']
  port 5432
  action :create
end

postgresql_user 'dbfit' do
  password 'dbfit'
  createdb true
  action :create
end

postgresql_database 'dbfit' do
  owner 'dbfit'
  action :create
end

execute 'update env to allow access from host' do
  project_root = node['dbfit']['project_root']

  user 'postgres'
  command "#{project_root}/test_vm/scripts/postgresql/update_env.sh"
  not_if { ::File.exist?('/etc/vm_provision_on_postgres_timestamp') }
  action :run
  returns [0, 2]
end

service 'postgresql-9.6.service' do
  action :restart
end