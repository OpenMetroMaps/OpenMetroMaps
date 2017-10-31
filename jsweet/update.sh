#!/bin/bash

set -e

gradle jsweet
cp -a build/target/* webapp/
