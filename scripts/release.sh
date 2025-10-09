#!/usr/bin/env bash
set -euo pipefail

BUMP="${1:-patch}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
POM="$ROOT_DIR/product/pom.xml"

# Get current version
CURR=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" "$POM" 2>/dev/null || true)
if [[ -z "$CURR" ]]; then
  echo "Cannot read version from $POM"; exit 1
fi

IFS='.-' read -r MAJ MIN PAT SNAP <<<"$CURR"
if [[ "$BUMP" == "major" ]]; then
  MAJ=$((MAJ+1)); MIN=0; PAT=0
elif [[ "$BUMP" == "minor" ]]; then
  MIN=$((MIN+1)); PAT=0
else
  PAT=$((PAT+1))
fi
NEW="${MAJ}.${MIN}.${PAT}-SNAPSHOT"
TAG="v${MAJ}.${MIN}.${PAT}"

# Update pom version
mvn -q -f "$ROOT_DIR/product/pom.xml" versions:set -DnewVersion="$NEW" -DgenerateBackupPoms=false || true

git add "$POM"
git commit -m "chore(release): bump version to $NEW"
git tag -a "$TAG" -m "Release $TAG"

# Build/publish images
"$ROOT_DIR/scripts/docker/build-base.sh" "$TAG"
"$ROOT_DIR/scripts/docker/build-product.sh" "$TAG"
"$ROOT_DIR/scripts/docker/publish.sh" "$TAG"

echo "Release complete: $TAG (next snapshot $NEW)"
