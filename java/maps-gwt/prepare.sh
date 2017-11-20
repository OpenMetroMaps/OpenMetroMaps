#!/bin/bash

gradle depunpack

DIR="build/unpackedJars"

cp UnpackedJars.gwt.xml "$DIR"

rm "$DIR/jama/MatrixIO.java"

~/github/sebkur/javaparser-transforms/scripts/remove-externalizable.sh "$DIR"
~/github/sebkur/javaparser-transforms/scripts/replace-string-format.sh "$DIR"

COORDINATE="$DIR/com/vividsolutions/jts/geom/Coordinate.java"
~/github/sebkur/javaparser-transforms/scripts/replace-method.sh clone \
    "public Object clone() { return new Coordinate(this); }" \
    "$COORDINATE"

VECTOR="$DIR/de/topobyte/lightgeom/lina/Vector2.java"
~/github/sebkur/javaparser-transforms/scripts/remove-method.sh fastInverseNorm "$VECTOR"
~/github/sebkur/javaparser-transforms/scripts/remove-method.sh fastInverseSquareRoot "$VECTOR"
~/github/sebkur/javaparser-transforms/scripts/remove-method.sh normalizeFast "$VECTOR"

BBOX="$DIR/de/topobyte/adt/geo/BBox.java"
~/github/sebkur/javaparser-transforms/scripts/remove-method.sh toEnvelope "$BBOX"
~/github/sebkur/javaparser-transforms/scripts/remove-constructor.sh Envelope "$BBOX"
~/github/sebkur/javaparser-transforms/scripts/remove-import.sh Envelope "$BBOX"

JAMA_MATRIX="$DIR/jama/Matrix.java"
~/github/sebkur/javaparser-transforms/scripts/remove-method-annotation.sh clone Override "$JAMA_MATRIX"

MATRIX="$DIR/de/topobyte/lina/Matrix.java"
~/github/sebkur/javaparser-transforms/scripts/substitute.sh \
    "System.getProperty(\"line.separator\")" "\"\n\"" "$MATRIX"

TINTOBJECTHASHMAP="$DIR/com/slimjars/dist/gnu/trove/map/hash/TIntObjectHashMap.java"
~/github/sebkur/javaparser-transforms/scripts/substitute.sh \
    "dest = (V[]) java.lang.reflect.Array.newInstance(dest.getClass().getComponentType(), _size);" \
    "dest = (V[]) new Object[_size];" \
    "$TINTOBJECTHASHMAP"
~/github/sebkur/javaparser-transforms/scripts/substitute.sh \
    "a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);" \
    "a = (T[]) new Object[size];" \
    "$TINTOBJECTHASHMAP"
