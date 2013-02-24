require 'rake/clean'
require 'rake/packagetask'

VERSION = File.read('dbfit-java/pom.xml').scan(/<version>(.*?)<\/version>/)[0][0]

CLOBBER << 'dist'
directory 'dist'
directory 'dist/lib'
directory 'dist/fitsharp'

task :copy_local => ['dist', 'dist/lib'] do
  cp_r FileList['LICENSE', 'README.md', 'bin/*', 'FitNesseRoot'], 'dist'
  jars_to_distribute = FileList['dbfit-java/**/*.jar'].
                         exclude('**/fitnesse*.jar').
                         exclude('dbfit-java/teradata/**/*.jar').
                         exclude('**/junit*.jar').
                         exclude('**/ojdbc*.jar')
  cp_r jars_to_distribute, 'dist/lib'
end

task :bundle_fitsharp => ['dist/fitsharp'] do
  fitsharp_url = 'https://github.com/downloads/jediwhale/fitsharp/release.2.2.net.40.zip'
  sh "wget \"#{fitsharp_url}\" -O dist/fitsharp/fitsharp.zip"
  sh "unzip dist/fitsharp/fitsharp.zip -d dist/fitsharp"
  sh "rm dist/fitsharp/fitsharp.zip"
  sh 'echo "<configuration><runtime><loadFromRemoteSources enabled=\"true\"/></runtime></configuration>" > dist/fitsharp/Runner.exe.config'
end

task :bundle_fitnesse => ['dist/lib'] do
  fitnesse_url = 'https://cleancoder.ci.cloudbees.com/job/fitnesse/278/artifact/dist/fitnesse-standalone.jar'
  sh "wget \"#{fitnesse_url}\" -O dist/lib/fitnesse-standalone.jar"
end

task :package => [:clobber, :copy_local, :bundle_fitnesse, :bundle_fitsharp] do
  cd 'dist' do
    archive_name = "dbfit-complete-#{VERSION}.zip"
    sh "zip -r #{archive_name} *"
  end
end
