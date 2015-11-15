#!/usr/bin/env bash

if [ "$DC_CLI_HOME" = "" ]; then
    echo "Need to set DC_CLI_HOME"
    exit 1
fi

if [ ! -d "$DC_CLI_HOME/jre" ]
then
    if [ "$JAVA_HOME" = "" ]; then
        echo "Need to set JAVA_HOME"
        exit 1
    fi
    CLI_JAVA_HOME=$JAVA_HOME
else
    CLI_JAVA_HOME=${DC_CLI_HOME}/jre
fi

echo $CLI_JAVA_HOME

$CLI_JAVA_HOME/bin/java -cp ${DC_CLI_HOME}/lib/* com.dc.DivineCloudCli $*