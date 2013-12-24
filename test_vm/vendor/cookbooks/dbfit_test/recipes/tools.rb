include_recipe "dbfit_test::environment"
include_recipe "java"

package "git-core" do
  action :install
end
