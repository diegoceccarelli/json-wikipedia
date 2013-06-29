#!/usr/bin/env bash

EXPECTED_ARGS=2
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` xml-dump  new-xml-dump"
  exit $E_BADARGS
fi

WIKI_XML_DUMP=$1
NEW_XML_DUMP=$2


echo "converting the xml dump to generate a new xml dump with one article per line"
tr '\n\t' ' ' <  $WIKI_XML_DUMP | sed  "s/<page>/	/g" | tr '\t' '\n' > $NEW_XML_DUMP
