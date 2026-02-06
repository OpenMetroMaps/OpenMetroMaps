#!/bin/bash
set -eu

BVG_TOOLS="$HOME/github/topobyte/bvg/scripts/"
DEST_DIR="../src/main/resources/res/images"

if [ ! -d "$BVG_TOOLS" ]; then
  echo "BVG tools not found at: $BVG_TOOLS" >&2
  echo "Install BVG (https://github.com/topobyte/bvg) and set BVG_TOOLS to its scripts directory." >&2
  exit 1
fi

convert() {
  local dir="$1"
  local name="$2"
  local svg="${dir}/${name}"
  local bvg="${DEST_DIR}/${dir}/${name%.svg}.bvg"

  if [ -f "$bvg" ]; then
    echo "Skipping (exists): $bvg"
    return 0
  fi

  echo "$svg -> $bvg"
  $BVG_TOOLS/SvgToBvg --compress deflate "$svg" "$bvg"
}

mkdir -p "$DEST_DIR/material-symbols"
mkdir -p "$DEST_DIR/openmetromaps"

convert "material-symbols" "undo.svg"
convert "material-symbols" "redo.svg"
convert "material-symbols" "note-add-outline.svg"
convert "material-symbols" "save-outline.svg"
convert "material-symbols" "save-as-outline.svg"
convert "material-symbols" "folder-open-outline.svg"
convert "material-symbols" "exit-to-app.svg"
convert "material-symbols" "settings-outline.svg"
convert "material-symbols" "description-outline.svg"
convert "material-symbols" "info-outline.svg"
convert "material-symbols" "gavel.svg"
convert "material-symbols" "manufacturing.svg"
convert "material-symbols" "blur-on.svg"
convert "material-symbols" "blur-off.svg"

convert "openmetromaps" "align-vertically.svg"
convert "openmetromaps" "align-horizontally.svg"
