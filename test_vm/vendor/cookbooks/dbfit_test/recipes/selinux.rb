selinux_state 'SELinux Permissive Temporary' do
  temporary true
  action :permissive
end

selinux_state 'SELinux Permissive Permanent' do
  temporary false
  action :permissive
end
