
#!/usr/bin/env bash

EXPECTED_ARGS=2
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` xml-dump  json-dump"
  exit $E_BADARGS
fi

WIKI_XML_DUMP=$1
WIKI_JSON_DUMP=$2


echo "converting mediawiki xml dump to json dump ($WIKI_JSON_DUMP)"

$JAVA  it.isti.cnr.hpc.wikipedia.cli.MediawikiToJsonCLI -input $WIKI_XML_DUMP -output $WIKI_JSON_DUMP
