require 'rbconfig'

def windows?
  RbConfig::CONFIG['host_os'] =~ /mswin|mingw|cygwin|msys|windows/i
end

custom_config_file = File.expand_path("../vagrant_config_custom.rb", __FILE__)
CONFIG = Hash.new

# Load defaults
require_relative 'vagrant_config_defaults'

# Load local customizations if any
require_relative custom_config_file if File.exist?(custom_config_file)

def customize_config(cfg_group, cfg)
  CONFIG[cfg_group].each { |key, val| cfg.send("#{key}=", val) }
end
