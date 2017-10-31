#!/bin/bash

set -e

gradle jsweet

rm -rf webapp/javascript
rm -rf webapp/candies

cp -a build/target/* webapp/
