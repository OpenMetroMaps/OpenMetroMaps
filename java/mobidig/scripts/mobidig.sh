#!/bin/bash

DIR=$(dirname $0)
LIBS="$DIR/../build/lib-run"

if [ ! -d "$LIBS" ]; then
	echo "Please run 'gradle createRuntime'"
	exit 1
fi

CLASSPATH="$LIBS/*"

exec java -cp "$CLASSPATH" "$@"
