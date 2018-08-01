postgresql_host = "127.0.0.1"
postgresql_port = 5432
postgresql_username = 'postgres'
postgresql_password = node['postgresql']['password']['postgres']

test_database = 'dbfit'
test_username = 'dbfit'
test_password = 'dbfit'

sql_dir = File.join(node['dbfit']['project_root'], "test_vm/scripts/postgresql/sql")

postgresql_server_install 'Install Postgresql server' do
  action :install
end

postgresql_server_install 'Setup Postgresql server' do
  password postgresql_password
  port postgresql_port
  action :create
end

postgresql_client_install 'Setup Postgresql client' do
  action :install
end

postgresql_database test_database do
  host postgresql_host
  port postgresql_port
  user postgresql_username
  action :create
end

postgresql_user test_username do
  password test_password
  superuser true
  action :create
end

postgresql_access 'dbfit grants' do
  access_type 'local'
  access_user test_username
  access_db test_database
  access_method 'md5'
  action :grant
end

execute "Create dbdeploy changelog table" do
    command "psql -U #{postgresql_username} -d #{test_database} -f #{sql_dir}/create-db-schema-postgresql.sql"
end
