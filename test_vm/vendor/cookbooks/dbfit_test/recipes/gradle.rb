project_root = node['dbfit']['project_root']
  
file "#{project_root}/gradle.properties" do
  content "excludeIntegrationTests=netezza,teradata\n"
  mode '0644'
  owner 'vagrant'
  group 'vagrant'
end
