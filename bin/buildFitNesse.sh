#!/usr/bin/env sh

cd dbfit-java
mvn clean

# install the root POM
mvn -N install

# build and install the core jar
cd core
mvn install

# package everything else
cd ..
mvn package

