require 'rake/clean'
require 'rake/packagetask'

VERSION = File.read('dbfit-java/pom.xml').scan(/<version>(.*?)<\/version>/)[0][0]

CLOBBER << 'dist'
directory 'dist'
directory 'dist/lib'

task :copy_local => ['dist', 'dist/lib'] do
  cp_r FileList['LICENSE', 'README.md', 'bin/*', 'FitNesseRoot'], 'dist'
  jars_to_distribute = FileList['dbfit-java/**/*.jar'].
                         exclude('**/fitnesse*.jar').
                         exclude('**/junit*.jar').
                         exclude('**/ojdbc*.jar')
  cp_r jars_to_distribute, 'dist/lib'
end

task :copy_fitnesse => ['dist/lib'] do
  fitnesse_url = 'http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20121220'
  sh "wget \"#{fitnesse_url}\" -O dist/lib/fitnesse-standalone.jar"
end

task :package => [:clobber, :copy_local, :copy_fitnesse] do
  cd 'dist' do
    archive_name = "dbfit-complete-#{VERSION}.zip"
    sh "zip -r #{archive_name} *"
  end
end
