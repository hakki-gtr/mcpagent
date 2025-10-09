#!/usr/bin/env bash
set -euo pipefail
VERSION="${1:-dev}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# Build app JAR
( cd "$ROOT_DIR/product" && ./mvnw -q -DskipTests package || mvn -q -DskipTests package )

docker build -f "$ROOT_DIR/Dockerfile" \
  --build-arg APP_JAR=product/target/mcpagent-0.1.0-SNAPSHOT.jar \
  --build-arg BASE_IMAGE=admingentoro/base:latest \
  -t admingentoro/mcpagent:$VERSION -t admingentoro/mcpagent:latest "$ROOT_DIR"

echo "Built admingentoro/mcpagent:$VERSION"
