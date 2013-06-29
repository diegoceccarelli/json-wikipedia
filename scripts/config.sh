#!/usr/bin/env bash

VERSION="0.0.3-SNAPSHOT"
XMX="-Xmx8000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp ./target/wikipedia-helper-$VERSION-jar-with-dependencies.jar "

WIKI_XML_DUMP=/data/wikipedia/itwiki-20121012/itwiki-20121012-pages-meta-current.xml.bz2
WIKI_JSON_DUMP=/data/wikipedia/itwiki-20121012/itwiki-20121012-pages-meta-current.json.gz




export LC_ALL=C
