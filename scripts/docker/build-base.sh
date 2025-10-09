#!/usr/bin/env bash
set -euo pipefail
VERSION="${1:-latest}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
docker build -f "$ROOT_DIR/Dockerfile.base" -t admingentoro/base:$VERSION -t admingentoro/base:latest "$ROOT_DIR"
echo "Built admingentoro/base:$VERSION"
