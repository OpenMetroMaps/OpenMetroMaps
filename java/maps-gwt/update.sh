#!/bin/bash

rm -rf test
gradle compileGwt
mkdir test
cp -a build/gwt/out/* test
cp war/berlin.xml war/index.html test
