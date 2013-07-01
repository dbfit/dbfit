#!/bin/sh
cd `dirname $0`
java -cp "lib/*" fitnesseMain.FitNesseMain -p 8085 -e 0 -o $1 $2 $3 $4 $5
