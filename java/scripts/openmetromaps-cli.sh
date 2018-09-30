#!/bin/bash

DIR=$(dirname $0)
LIBS="$DIR/../cli/build/lib-run"

if [ ! -d "$LIBS" ]; then
	echo "Please run 'gradle createRuntime'"
	exit 1
fi

function join { local IFS="$1"; shift; echo "$*"; }

CP="$LIBS/*"
CLASSPATH=$(join ':' $CP)

exec java -cp "$CLASSPATH" "$@"
