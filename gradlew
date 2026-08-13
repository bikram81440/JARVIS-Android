#!/usr/bin/env sh
set -eu

GRADLE_VERSION="9.3.1"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin"
INSTALL_DIR="$DIST_DIR/gradle-$GRADLE_VERSION"
GRADLE_BIN="$INSTALL_DIR/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$DIST_DIR"
  ZIP="$DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
  if [ ! -f "$ZIP" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fL --retry 3 -o "$ZIP" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ZIP" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    else
      echo "ERROR: curl or wget is required to download Gradle $GRADLE_VERSION." >&2
      exit 1
    fi
  fi
  TMP="$DIST_DIR/.extract-$$"
  rm -rf "$TMP"
  mkdir -p "$TMP"
  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$ZIP" -d "$TMP"
  else
    echo "ERROR: unzip is required to extract Gradle." >&2
    exit 1
  fi
  rm -rf "$INSTALL_DIR"
  mv "$TMP/gradle-$GRADLE_VERSION" "$INSTALL_DIR"
  rm -rf "$TMP"
fi

exec "$GRADLE_BIN" "$@"
