
#!/usr/bin/env bash

EXPECTED_ARGS=2
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` json-dump  summary"
  exit $E_BADARGS
fi

WIKI_JSON_DUMP=$1
WIKI_SUMMARY=$2


echo " producing summary  $WIKI_JSON_DUMP -> $WIKI_SUMMARY"

$JAVA  it.cnr.isti.hpc.wikipedia.cli.GetDumpSummaryCLI -input $WIKI_JSON_DUMP -output $WIKI_SUMMARY
