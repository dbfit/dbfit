#!/usr/bin/env sh
cd "`dirname $0`"
java -cp 'lib/*' dbfit.util.crypto.CryptoApp $@

