include_recipe "dbfit_test::2gb_swapfile"

package "libaio"
package "bc"
package "unzip"

execute 'install Oracle package if available' do
  project_root = node['dbfit']['project_root']

  user 'root'
  command "#{project_root}/dbfit-java/oracle/src/integration-test/resources/install_oracle.sh"
  not_if "grep -q '^XE' /etc/oratab"
  action :run
  returns [0, 2]
end

