#!/bin/sh

# setup FitNesse

wget https://cleancoder.ci.cloudbees.com/job/fitnesse/278/artifact/dist/fitnesse.jar -O /tmp/fitnesse.jar

wget http://repo1.maven.org/maven2/org/fitnesse/fitnesse/20121220/fitnesse-20121220.pom -O /tmp/fitnesse-20130216.pom

sed -i 's/20121220/20130216/g' /tmp/fitnesse-20130216.pom

mvn install:install-file -Dfile=/tmp/fitnesse.jar \
  -DgroupId=org.fitnesse -DartifactId=fitnesse \
  -Dversion=20130216 -Dpackaging=jar -DpomFile=/tmp/fitnesse-20130216.pom

# setup FitLibrary

wget https://s3.amazonaws.com/dbfit/fitlibrary-20081102.jar -O /tmp/fitlibrary.jar

mvn install:install-file -Dfile=/tmp/fitlibrary.jar -DgroupId=org.fitnesse \
    -DartifactId=fitlibrary -Dversion=20081102 -Dpackaging=jar