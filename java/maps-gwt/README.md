To get things going, you currently need to do the following:

    gradle depunpack
    cp UnpackedJars.gwt.xml build/unpackedJars/
    ~/github/sebkur/javaparser-transform-tests/scripts/remove-externalizable.sh build/unpackedJars/
    ~/github/sebkur/javaparser-transform-tests/scripts/replace-string-format.sh build/unpackedJars/
    ~/github/sebkur/javaparser-transform-tests/scripts/replace-method.sh clone \
        "public Object clone() { return new Coordinate(this); }" \
        build/unpackedJars/com/vividsolutions/jts/geom/Coordinate.java
