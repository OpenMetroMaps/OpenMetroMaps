To get things going, you currently need to do the following:

    gradle depunpack
    cp UnpackedJars.gwt.xml build/unpackedJars/
    ~/github/sebkur/javaparser-transform-tests/scripts/remove-externalizable.sh build/unpackedJars/
