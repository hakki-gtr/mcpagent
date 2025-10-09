#!/usr/bin/env bash
set -euo pipefail
VERSION="${1:-dev}"

for img in admingentoro/base admingentoro/mcpagent; do
  echo "Pushing $img:$VERSION"
  docker push "$img:$VERSION"
  if [[ "$img" == "admingentoro/mcpagent" && "$VERSION" != "latest" ]]; then
    docker push "$img:latest" || true
  fi
  if [[ "$img" == "admingentoro/base" && "$VERSION" != "latest" ]]; then
    docker push "$img:latest" || true
  fi
done
