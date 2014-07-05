#!/usr/bin/env ruby

def replace_pattern(filepath, pattern, replacement)
  content = File.open(filepath) { |f| f.read.gsub pattern, replacement }
  IO.write(filepath, content)
end

def set_version_in_gradle_build(version)
  replace_pattern 'build.gradle',
                  /(^ext.dbfitVersion)\s*=\s*'(.*)'/,
                  "\\1 = '#{version}'"
end

def set_version_in_docs(version)
  replace_pattern 'website/_config.yml',
                  /(^dbfit_version):\s*"(.*)"/,
                  "\\1: \"#{version}\""
end

def set_version(version)
  set_version_in_gradle_build(version)
  set_version_in_docs(version)
end

set_version ARGV[0]

