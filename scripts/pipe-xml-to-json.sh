#!/usr/bin/env bash

LANG=C

EXPECTED_ARGS=3
E_BADARGS=65

source scripts/config.sh


set -a

if [ "$( tty )" == 'not a tty' ]
then
    STDIN_DATA_PRESENT=1
else
    STDIN_DATA_PRESENT=0
fi

if [ ${STDIN_DATA_PRESENT} -eq 1 ]
then

    if [ $# -ne $EXPECTED_ARGS ];
    then
      echo "Usage: PIPED_XML_DATA | `basename $0` lang[en,it]  json-dump  num-threads"
      exit $E_BADARGS
    fi

    LANG=$1
    WIKI_JSON_DUMP=$2
    NUM_THREADS=$3


    echo "piping mediawiki to json dump ($WIKI_JSON_DUMP)"

    $JAVA  it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonPipeCLI -output $WIKI_JSON_DUMP -lang $LANG -threads $NUM_THREADS

else
    echo "No piped input stream found."
fi

