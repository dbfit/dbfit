include_recipe "mysql::server"
include_recipe "mysql::client"
include_recipe "database::mysql"

mysql_connection_info = {:host => "localhost",
                         :username => 'root',
                         :password => node['mysql']['server_root_password']}

users = {"dftest" => "dftest", "dbfit_user" => "password"}

mysql_database 'dbfit' do
  connection mysql_connection_info
  action :create
end

users.each do |username, password|
  mysql_database_user username do
    connection mysql_connection_info
    password password
    action :create
  end

  %w{localhost 127.0.0.1}.each do |hostname|
    mysql_database_user username do
      host hostname
      database_name 'dbfit'
      privileges [:all]
      action :grant
    end
  end
end

%w{localhost 127.0.0.1}.each do |hostname|
  mysql_database_user 'dbfit_user' do
    host hostname
    database_name 'mysql'
    privileges [:select]
    action :grant
  end
end