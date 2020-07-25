include_recipe 'yum-mysql-community::mysql57'

mysql2_chef_gem 'default' do
  gem_version '0.4.5'
  package_version node['mysql']['version'] if node['mysql']
  action :install
end

mysql_connection_info = {:host => "127.0.0.1",
                         :username => 'root',
                         :password => node['mysql']['server_root_password']}

users = {"dftest" => "dftest", "dbfit_user" => "password"}

mysql_service 'default' do
  version node['mysql']['version'] if node['mysql'] && node['mysql']['version']
  # allow for external access, e.g. from host connect to ip from host adapter
  bind_address "0.0.0.0"
  initial_root_password mysql_connection_info[:password]
  action [:create, :start]
end

# the database cookbook is deprecated and thus we execution the installation using mysql client via bash 
bash "Creating database dbfit" do
  not_if("/usr/bin/mysql -uroot -p#{node['mysql']['server_root_password']} -e'show databases' | grep dbfit", :user => 'root')
  user "root"
  code <<-EOM
    mysql -uroot -p#{node['mysql']['server_root_password']} -e 'create database dbfit'
  EOM
end

users.each do |username, password|
  bash "Creating users" do
    not_if("/usr/bin/mysql -uroot -p#{node['mysql']['server_root_password']} -e'select user from mysql.user' | grep #{username}", :user => 'root')
    user "root"
    code <<-EOM
      mysql -uroot -p#{node['mysql']['server_root_password']} -e "CREATE USER '#{username}'@'%' IDENTIFIED BY '#{password}'"
      mysql -uroot -p#{node['mysql']['server_root_password']} -e "GRANT ALL PRIVILEGES ON *.* TO '#{username}'@'%';FLUSH PRIVILEGES;"
    EOM
  end
end

bash "Creating changelog" do
  not_if("/usr/bin/mysql -uroot -p#{node['mysql']['server_root_password']} -e'use dbfit; show tables;' | grep changelog", :user => 'root')
  user "root"
  code <<-EOM
    mysql -uroot -p#{node['mysql']['server_root_password']} -e '
      CREATE TABLE IF NOT EXISTS dbfit.changelog (
        change_number INTEGER NOT NULL,
        complete_dt TIMESTAMP NOT NULL,
        applied_by VARCHAR(100) NOT NULL,
        description VARCHAR(500) NOT NULL,
        CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
      );
    '
  EOM
end

bash "Create testobjects" do
  not_if("/usr/bin/mysql -uroot -p#{node['mysql']['server_root_password']} -e'use dbfit; show tables;' | grep users", :user => 'root')
  user "root"
  code <<-EOM
    mysql -uroot -p#{node['mysql']['server_root_password']} < /var/dbfit/dbfit-java/mysql/build/resources/integrationTest/001_add_objects_needed_for_acceptance_test.sql
  EOM
end
