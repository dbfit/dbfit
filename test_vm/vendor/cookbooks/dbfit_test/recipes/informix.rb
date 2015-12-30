execute 'install Informix package if available' do
  project_root = node['dbfit']['project_root']

  user 'root'
  command "#{project_root}/dbfit-java/informix/src/integration-test/resources/install_informix.sh"
  not_if "#{project_root}/dbfit-java/informix/src/integration-test/resources/detect_informix.sh"
  action :run
  returns [0, 2, 3]
end

