chef_gem 'di-ruby-lvm'

lvm_physical_volume '/dev/sdb' do
  action :create
end

lvm_volume_group 'centos' do
  action :extend
  physical_volumes ['/dev/sdb']
end

lvm_logical_volume 'root' do
  group 'centos'
  action :resize
  size '9G'
  take_up_free_space true
end
