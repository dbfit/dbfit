include_recipe "dbfit_test::2gb_swapfile"

package "libaio"
package "numactl"
# not working anymore, so it is now installed using yum
#package "libstdc++.i686"

execute 'install DB2 package if available' do
  project_root = node['dbfit']['project_root']

  user 'root'
  command "#{project_root}/test_vm/scripts/db2/install_db2.sh"
  # command "#{project_root}/test_vm/scripts/db2/install_db2.sh -dl"
  not_if "#{project_root}/test_vm/scripts/db2/detect_db2.sh"
  action :run
  returns [0, 2, 3]
end

