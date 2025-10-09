#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPTS_DIR="$ROOT_DIR/scripts"
DOCKER_DIR="$SCRIPTS_DIR/docker"
OPENAPI_DIR="$SCRIPTS_DIR/openapi"

usage() {
  cat <<EOF
Usage: ./scripts/make.sh <command> [options]

Commands:
  build-base [VERSION]            Build base image (default VERSION=latest)
  build-product [VERSION]         Build product image (default VERSION=dev)
  publish [VERSION]               Push both images (base+product) with VERSION (and latest/dev tags)
  openapi [SPEC]                  Generate TS clients from OpenAPI spec into product/resources/typescript-runtime/generated
  release [patch|minor|major]     Bump version, tag, build & publish images
  test                            Run unit tests
  run                             Run Spring Boot app locally (dev)
  validate                        Run validate mode
EOF
}

cmd="${1:-}"; shift || true

case "${cmd}" in
  build-base)
    VERSION="${1:-latest}"
    "$DOCKER_DIR/build-base.sh" "$VERSION"
    ;;
  build-product)
    VERSION="${1:-dev}"
    "$DOCKER_DIR/build-product.sh" "$VERSION"
    ;;
  publish)
    VERSION="${1:-dev}"
    "$DOCKER_DIR/publish.sh" "$VERSION"
    ;;
  openapi)
    SPEC="${1:-${FOUNDATION_DIR:-/var/foundation}/apis/openapi.yaml}"
    "$OPENAPI_DIR/generate-ts.sh" "$SPEC"
    ;;
  release)
    BUMP="${1:-patch}"
    "$SCRIPTS_DIR/release.sh" "$BUMP"
    ;;
  test)
    (cd "$ROOT_DIR/product" && ./mvnw -q -DskipTests=false test || mvn -q -DskipTests=false test)
    ;;
  run)
    (cd "$ROOT_DIR/product" && ./mvnw -q spring-boot:run || mvn -q spring-boot:run)
    ;;
  validate)
    (cd "$ROOT_DIR/product" && ./mvnw -q -DskipTests package || mvn -q -DskipTests package)
    java -jar "$ROOT_DIR/product/target/mcpagent-0.1.0-SNAPSHOT.jar" --process=validate
    ;;
  *)
    usage; exit 1;;
esac
