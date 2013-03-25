include_recipe "maven"
include_recipe "gradle"
yum_package "rubygem-rake" do
    action :install
end

yum_package "zip" do
    action :install
end

package "git-core" do
  action :install
end
