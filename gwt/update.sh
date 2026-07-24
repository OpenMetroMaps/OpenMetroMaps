#!/bin/bash

set -e

DIR=$(dirname $0)
OUT="build/gwt"
WAR="$OUT/war"
BOOTSTRAP_VERSION="4.6.2"
BOOTSTRAP_CACHE="$DIR/build/vendor/bootstrap-$BOOTSTRAP_VERSION/css"
BOOTSTRAP_TEST="test/vendor/bootstrap-$BOOTSTRAP_VERSION/css"
BOOTSTRAP_BASE_URL="https://raw.githubusercontent.com/twbs/bootstrap/v$BOOTSTRAP_VERSION/dist/css"

prepare_bootstrap()
{
	mkdir -p "$BOOTSTRAP_CACHE"

	if [ ! -f "$BOOTSTRAP_CACHE/bootstrap.min.css" ]; then
		curl -fsSL "$BOOTSTRAP_BASE_URL/bootstrap.min.css" \
			-o "$BOOTSTRAP_CACHE/bootstrap.min.css"
	fi

	if [ ! -f "$BOOTSTRAP_CACHE/bootstrap.min.css.map" ]; then
		curl -fsSL "$BOOTSTRAP_BASE_URL/bootstrap.min.css.map" \
			-o "$BOOTSTRAP_CACHE/bootstrap.min.css.map"
	fi
}

rm -rf "$OUT"
rm -rf test
"$DIR/gradlew" gwtCompile
mkdir test

OUTPUT="demo simple scrollable_simple scrollable_advanced"
for MODULE in $OUTPUT; do
	cp -a "$WAR/$MODULE" test
done

cp src/main/webapp/berlin.omm src/main/webapp/*.html test
prepare_bootstrap
mkdir -p "$BOOTSTRAP_TEST"
cp "$BOOTSTRAP_CACHE/bootstrap.min.css" "$BOOTSTRAP_TEST"
cp "$BOOTSTRAP_CACHE/bootstrap.min.css.map" "$BOOTSTRAP_TEST"
