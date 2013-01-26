include_recipe "mysql::server"
include_recipe "mysql::client"
include_recipe "database::mysql"

mysql_database 'dbfit' do
  connection ({:host => "localhost", :username => 'root', :password => node['mysql']['server_root_password']})
  action :create
end