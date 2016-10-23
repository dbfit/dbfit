mysql2_chef_gem 'default' do
  gem_version '0.4.4'
  client_version node['mysql']['version'] if node['mysql']
  action :install
end

mysql_connection_info = {:host => "127.0.0.1",
                         :username => 'root',
                         :password => node['mysql']['server_root_password']}

users = {"dftest" => "dftest", "dbfit_user" => "password"}

mysql_service 'default' do
  version node['mysql']['version'] if node['mysql'] && node['mysql']['version']
  bind_address mysql_connection_info[:host]
  initial_root_password mysql_connection_info[:password]
  action [:create, :start]
end

mysql_database 'dbfit' do
  connection mysql_connection_info
  action :create
end

users.each do |username, password|
  mysql_database_user "create #{username}" do
    connection mysql_connection_info
    username username
    password password
    action :create
  end

  %w{localhost 127.0.0.1}.each do |hostname|
    mysql_database_user "grants on dbfit.* to #{username}@#{hostname}" do
      connection mysql_connection_info
      username username
      password password
      host hostname
      database_name 'dbfit'
      privileges [:all]
      action :grant
    end
  end
end

%w{localhost 127.0.0.1}.each do |hostname|
  mysql_database_user "grants on mysql.* to dbfit_user@#{hostname}" do
    connection mysql_connection_info
    username "dbfit_user"
    password users["dbfit_user"]
    host hostname
    database_name 'mysql'
    privileges [:select]
    action :grant
  end
end

# needed to support DbDeploy
mysql_database 'create dbfit.changelog table' do
  database_name 'dbfit'
  connection(
    :host => mysql_connection_info[:host],
    :username => 'dbfit_user',
    :password => users['dbfit_user']
  )
  sql "CREATE TABLE IF NOT EXISTS changelog (
        change_number INTEGER NOT NULL,
        complete_dt TIMESTAMP NOT NULL,
        applied_by VARCHAR(100) NOT NULL,
        description VARCHAR(500) NOT NULL,
        CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
      );"
  action :query
end

