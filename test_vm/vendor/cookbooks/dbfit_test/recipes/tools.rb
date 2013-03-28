include_recipe "maven"
include_recipe "gradle"

package "git-core" do
  action :install
end
