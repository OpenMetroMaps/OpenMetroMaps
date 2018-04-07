#!/bin/bash

set -e

DIR=$(dirname $0)
OUTPUT="release"

cd "$DIR"

rm -rf "$OUTPUT"

./gradlew clean releaseJars
