require 'rbconfig'

def windows?
  RbConfig::CONFIG['host_os'] =~ /mswin|mingw|cygwin|msys|windows/i
end

class DbFit
  def self.customize_config(cfg_group, &block)
    CONFIG[cfg_group] = block
  end

  def self.apply_custom_config(cfg_group, target_config)
    CONFIG[cfg_group].call(target_config) if CONFIG[cfg_group].is_a? Proc
  end
end

custom_config_file = File.expand_path("../vagrant_config_custom.rb", __FILE__)
CONFIG = Hash.new

# Load defaults
require_relative 'vagrant_config_defaults'

# Load local customizations if any
require_relative custom_config_file if File.exist?(custom_config_file)

