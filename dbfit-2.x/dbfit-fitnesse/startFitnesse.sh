#!/bin/sh
mvn clean dependency:copy-dependencies package 
java -cp target/dependency/fitnesse-20081201.jar fitnesse.FitNesse -p 8085 -e 0 -d src/main/fitnesse -o $1 $2 $3 $4 $5


