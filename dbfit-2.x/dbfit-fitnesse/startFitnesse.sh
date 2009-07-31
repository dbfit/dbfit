#!/bin/sh
#mvn clean dependency:copy-dependencies -Dmdep.stripVersion=true package 
cd target/dependency
#java -jar fitnesse.jar
java -jar fitnesse.jar -p 8085 -e 0 -d ../../src/main/fitnesse -o $1 $2 $3 $4 $5


