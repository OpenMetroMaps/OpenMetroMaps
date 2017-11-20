#!/bin/bash

gradle depunpack

DIR="build/unpackedJars"

cp UnpackedJars.gwt.xml "$DIR"
~/github/sebkur/javaparser-transforms/scripts/remove-externalizable.sh "$DIR"
~/github/sebkur/javaparser-transforms/scripts/replace-string-format.sh "$DIR"
~/github/sebkur/javaparser-transforms/scripts/replace-method.sh clone \
    "public Object clone() { return new Coordinate(this); }" \
    "$DIR/com/vividsolutions/jts/geom/Coordinate.java"
rm "$DIR/jama/MatrixIO.java"
