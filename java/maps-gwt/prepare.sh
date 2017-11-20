#!/bin/bash

gradle depunpack

DIR="build/unpackedJars"

cp UnpackedJars.gwt.xml "$DIR"
~/github/sebkur/javaparser-transform-tests/scripts/remove-externalizable.sh "$DIR"
~/github/sebkur/javaparser-transform-tests/scripts/replace-string-format.sh "$DIR"
~/github/sebkur/javaparser-transform-tests/scripts/replace-method.sh clone \
    "public Object clone() { return new Coordinate(this); }" \
    "$DIR/com/vividsolutions/jts/geom/Coordinate.java"
rm "$DIR/jama/MatrixIO.java"
