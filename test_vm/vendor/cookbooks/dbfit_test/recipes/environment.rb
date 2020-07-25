template "/etc/profile.d/dbfit_env.sh" do
  source "dbfit_env.sh.erb"
  variables :project_root => node[:dbfit][:project_root]
  mode 0755
end



