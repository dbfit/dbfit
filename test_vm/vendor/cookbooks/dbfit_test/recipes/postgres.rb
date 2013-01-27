include_recipe "postgresql::server"
include_recipe "postgresql::client"
include_recipe "database::postgresql"

postgresql_connection_info = {:host => "127.0.0.1",
                              :port => 5432,
                              :username => 'postgres',
                              :password => node['postgresql']['password']['postgres']}

postgresql_database 'dbfit' do
  connection postgresql_connection_info
  action :create
end

# needed to support DbDeploy
postgresql_database 'dbfit' do
  connection postgresql_connection_info
  sql "CREATE TABLE IF NOT EXISTS changelog (
         change_number INTEGER NOT NULL,
         complete_dt TIMESTAMP NOT NULL,
         applied_by VARCHAR(100) NOT NULL,
         description VARCHAR(500) NOT NULL
       );

       ALTER TABLE changelog DROP CONSTRAINT Pkchangelog;

       ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number);

       ALTER TABLE changelog OWNER to dbfit;
       "
  action :query
end

postgresql_database_user 'dbfit' do
  connection postgresql_connection_info
  database_name 'dbfit'
  password 'dbfit'
  privileges [:all]
  action :create
  action :grant
end
