package "csh"

execute 'install Sybase IQ package if available' do
  project_root = node['dbfit']['project_root']

  user 'root'
  command "#{project_root}/test_vm/scripts/sybase/install_sybaseiq.sh -dl"
  not_if "#{project_root}/test_vm/scripts/sybase/detect_sybaseiq.sh"
  action :run
  returns [0, 2, 3]
end

execute 'install Sybase ASE package if available' do
  project_root = node['dbfit']['project_root']

  user 'root'
  command "#{project_root}/test_vm/scripts/sybase/install_sybasease.sh -dl"
  not_if "#{project_root}/test_vm/scripts/sybase/detect_sybasease.sh"
  action :run
  returns [0, 2, 3]
end
