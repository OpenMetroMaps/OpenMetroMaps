#!/bin/bash

set -e

DIR=$(dirname $0)
OUTPUT="release"

cd "$DIR"

rm -rf "$OUTPUT"

./gradlew clean jar

cp release-jar/build/libs -a "$OUTPUT"
