#!/usr/bin/env bash

VERSION="2.0.0-SNAPSHOT"
XMX="-Xmx8000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
JAVA="java $XMX -Dlogat=$LOGAT  -Dfile.encoding=UTF-8 -Dlog=$LOG -Dlogback.configurationFile=./logback.xml -cp ./target/json-wikipedia-$VERSION-jar-with-dependencies.jar "





export LC_ALL=C
