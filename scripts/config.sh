#!/usr/bin/env bash

VERSION="1.0.0"
XMX="-Xmx8000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp .:./target/json-wikipedia-$VERSION-jar-with-dependencies.jar "





export LC_ALL=C
