@echo off
set FITNESSE_PORT=8085
title Fitnesse on port %FITNESSE_PORT%
REM set JAVA_HOME = location_of_java

REM # For local running (without Maven installed)
REM "%JAVA_HOME%\bin\java" -cp ..\..\target\fitnesse.jar fitnesse.FitNesse -p %FITNESSE_PORT% -e 0 -d .. -o %1 %2 %3 %4 %5 
REM # When running from Maven-aware environments
set M2_REPO="%USERPROFILE%\.m2\repository"
set FITNESSE_VERSION=20081201
set FITNESSE_JAR=%M2_REPO%/org/fitnesse/fitnesse/%FITNESSE_VERSION%/fitnesse-%FITNESSE_VERSION%.jar
echo Using FitNesse JAR from %FITNESSE_JAR%
"%JAVA_HOME%\bin\java" -DFITNESSE_VERSION=%FITNESSE_VERSION% -DM2_REPO=%M2_REPO% -cp %FITNESSE_JAR% fitnesse.FitNesse -p %FITNESSE_PORT% -e 0 -d ../../main/fitnesse -o %1 %2 %3 %4 %5 

