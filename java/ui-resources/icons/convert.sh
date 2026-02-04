#!/bin/bash
set -eu

BVG_TOOLS="$HOME/github/topobyte/bvg/scripts/"
SRC_DIR="material-symbols"
DEST_DIR="../src/main/resources/res/images/material-symbols"

if [ ! -d "$BVG_TOOLS" ]; then
  echo "BVG tools not found at: $BVG_TOOLS" >&2
  echo "Install BVG (https://github.com/topobyte/bvg) and set BVG_TOOLS to its scripts directory." >&2
  exit 1
fi

convert() {
  local name="$1"
  local svg="${SRC_DIR}/${name}"
  local bvg="${DEST_DIR}/${name%.svg}.bvg"

  if [ -f "$bvg" ]; then
    echo "Skipping (exists): $bvg"
    return 0
  fi

  echo "$svg -> $bvg"
  $BVG_TOOLS/SvgToBvg --compress deflate "$svg" "$bvg"
}

mkdir -p "$DEST_DIR"

convert "undo.svg"
convert "redo.svg"
convert "note-add-outline.svg"
convert "save-outline.svg"
convert "save-as-outline.svg"
convert "folder-open-outline.svg"
convert "exit-to-app.svg"
convert "settings-outline.svg"
convert "description-outline.svg"
convert "info-outline.svg"
convert "gavel.svg"
