#!/bin/bash

DIR=$(dirname $0)
SRC_BERLIN="$DIR/../../../OpenMetroMapsData/berlin"

SRC_GEOGRAPHIC="$SRC_BERLIN/geographic.omm"
SRC_SCHEMATIC="$SRC_BERLIN/schematic.omm"

DEST="$DIR/test-data/src/main/resources"
DEST_GEOGRAPHIC="$DEST/berlin-geographic.omm"
DEST_SCHEMATIC="$DEST/berlin-schematic.omm"

cp "$SRC_GEOGRAPHIC" "$DEST_GEOGRAPHIC"
cp "$SRC_SCHEMATIC" "$DEST_SCHEMATIC"
