#!/bin/bash

gradle depunpack

JT="$HOME/github/sebkur/javaparser-transforms/scripts"
DIR="build/unpackedJars"

cp UnpackedJars.gwt.xml "$DIR"

rm "$DIR/jama/MatrixIO.java"

"$JT/remove-externalizable.sh" "$DIR"
"$JT/replace-string-format.sh" "$DIR"

COORDINATE="$DIR/com/vividsolutions/jts/geom/Coordinate.java"
"$JT/replace-method.sh" clone \
    "public Object clone() { return new Coordinate(this); }" \
    "$COORDINATE"

VECTOR="$DIR/de/topobyte/lightgeom/lina/Vector2.java"
"$JT/remove-method.sh" fastInverseNorm "$VECTOR"
"$JT/remove-method.sh" fastInverseSquareRoot "$VECTOR"
"$JT/remove-method.sh" normalizeFast "$VECTOR"

BBOX="$DIR/de/topobyte/adt/geo/BBox.java"
"$JT/remove-method.sh" toEnvelope "$BBOX"
"$JT/remove-constructor.sh" Envelope "$BBOX"
"$JT/remove-import.sh" Envelope "$BBOX"

JAMA_MATRIX="$DIR/jama/Matrix.java"
"$JT/remove-method-annotation.sh" clone Override "$JAMA_MATRIX"

MATRIX="$DIR/de/topobyte/lina/Matrix.java"
"$JT/substitute.sh" \
    "System.getProperty(\"line.separator\")" "\"\n\"" "$MATRIX"

TINTOBJECTHASHMAP="$DIR/com/slimjars/dist/gnu/trove/map/hash/TIntObjectHashMap.java"
"$JT/substitute.sh" \
    "dest = (V[]) java.lang.reflect.Array.newInstance(dest.getClass().getComponentType(), _size);" \
    "dest = (V[]) new Object[_size];" \
    "$TINTOBJECTHASHMAP"
"$JT/substitute.sh" \
    "a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);" \
    "a = (T[]) new Object[size];" \
    "$TINTOBJECTHASHMAP"
