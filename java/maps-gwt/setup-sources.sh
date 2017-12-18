#!/bin/bash

DIRS=(
    'maps-core'
    'maps-model'
    'maps-model-xml'
    'maps-model-xml-converter'
    'maps-model-util'
    'line-network-util'
    'lightgeom'
    'xml-dom-abstraction'
    'xml-dom-abstraction-gwt'
)

cd src/modules

for f in ${DIRS[@]}; do
    echo "$f"
    ln -sf "../../../$f/src/main/" "$f"
done
