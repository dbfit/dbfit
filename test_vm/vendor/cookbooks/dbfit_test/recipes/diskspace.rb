lvm_physical_volume '/dev/sdb' do
  action :create
end

lvm_volume_group 'VolGroup00' do
  action :extend
  physical_volumes ['/dev/sdb']
end

lvm_logical_volume 'LogVol00' do
  group 'VolGroup00'
  action :resize
  size '9G'
  take_up_free_space true
end
