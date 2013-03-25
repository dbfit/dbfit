require 'rake/clean'
require 'rake/packagetask'

VERSION = File.read('dbfit-java/build.gradle').scan(/version\ =\ \'(.*)\'/)[0][0]

CLOBBER << 'dist'
directory 'dist'
directory 'dist/lib'
directory 'dist/fitsharp'

task :cleanse_fitnesseroot do
  sh "git clean -d -x -f FitNesseRoot/"
end

task :copy_local => ['dist', 'dist/lib'] do
  cp_r FileList['LICENSE', 'README.md', 'bin/startFitnesse*', 'FitNesseRoot'], 'dist'
  jars_to_distribute = FileList['dbfit-java/**/*.jar'].
                         exclude('dbfit-java/build/**/*.jar').
                         exclude('dbfit-java/teradata/**/*.jar').
                         exclude('**/ojdbc*.jar')
  cp_r jars_to_distribute, 'dist/lib'
  cd 'dist/lib' do
    FileList['fitnesse-standalone*.jar'].each { |f| mv f, "fitnesse-standalone.jar" }
  end
end

task :bundle_fitsharp => ['dist/fitsharp'] do
  fitsharp_url = 'https://github.com/downloads/jediwhale/fitsharp/release.2.2.net.40.zip'
  sh "wget \"#{fitsharp_url}\" -O dist/fitsharp/fitsharp.zip"
  sh "unzip dist/fitsharp/fitsharp.zip -d dist/fitsharp"
  sh "rm dist/fitsharp/fitsharp.zip"
  sh 'echo "<configuration><runtime><loadFromRemoteSources enabled=\"true\"/></runtime></configuration>" > dist/fitsharp/Runner.exe.config'
end

task :package => [:clobber, :cleanse_fitnesseroot, :copy_local, :bundle_fitsharp] do
  cd 'dist' do
    archive_name = "dbfit-complete-#{VERSION}.zip"
    sh "zip -r #{archive_name} *"
  end
end
