#!/bin/bash

SVG="res/icon.svg"
TARGET="../java/ui-resources/src/main/resources/res/images/icon"

for size in 16 20 22 24 32 48 64 72 96 144; do
    echo $size;
    PNG="$TARGET/icon-$size.png"
    inkscape -C -w $size -e "$PNG" "$SVG"
done
