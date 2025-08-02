#!/bin/bash

set -e

DIR=$(dirname $0)
OUT="build/gwt"
WAR="$OUT/war"

rm -rf "$OUT"
rm -rf test
"$DIR/gradlew" gwtCompile
mkdir test

OUTPUT="demo simple scrollable_simple scrollable_advanced"
for DIR in $OUTPUT; do
	cp -a "$WAR/$DIR" test
done

cp src/main/webapp/berlin.xml src/main/webapp/*.html test
