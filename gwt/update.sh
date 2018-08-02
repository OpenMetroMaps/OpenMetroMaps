#!/bin/bash

DIR=$(dirname $0)

rm -rf test
"$DIR/gradlew" compileGwt
mkdir test

OUT="build/gwt/out"
OUTPUT=$(ls $OUT | grep -v WEB-INF)
for DIR in $OUTPUT; do
	cp -a "$OUT/$DIR" test
done

cp src/main/webapp/berlin.xml src/main/webapp/*.html test
