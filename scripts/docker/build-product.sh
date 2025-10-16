#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:-dev}"
JAR_NAME="${2:-}"
PUSH_FLAG="${3:-}"
PLATFORM_FLAG="${4:-}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
POM="$ROOT_DIR/src/mcpagent/pom.xml"

# Default platforms for multi-arch builds
PLATFORMS="${DOCKER_PLATFORMS:-linux/amd64,linux/arm64}"

# Handle platform flag
if [[ "$PLATFORM_FLAG" == "--platform" && -n "${5:-}" ]]; then
  PLATFORMS="${5}"
fi

# Get version from POM if JAR_NAME not provided
if [[ -z "$JAR_NAME" ]]; then
  POM_VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" "$POM" 2>/dev/null || true)
  if [[ -z "$POM_VERSION" ]]; then
    echo "Cannot read version from $POM"; exit 1
  fi
  JAR_NAME="mcpagent-$POM_VERSION.jar"
  
  # Build app JAR if not already built
  echo "Building application JAR..."
  ( cd "$ROOT_DIR/src/mcpagent" && ./mvnw -q -DskipTests package || mvn -q -DskipTests package )
fi

# Validate JAR exists
JAR_PATH="$ROOT_DIR/src/mcpagent/target/$JAR_NAME"
if [[ ! -f "$JAR_PATH" ]]; then
  echo "JAR file not found: $JAR_PATH"
  exit 1
fi

echo "Building product image with version: $VERSION"
echo "JAR: $JAR_NAME"
echo "Platforms: $PLATFORMS"

# Determine build command based on whether we're pushing
if [[ "$PUSH_FLAG" == "--push" ]]; then
  echo "Will push images to registry"
  BUILD_CMD="docker buildx build"
  BUILD_ARGS=(
    -f "$ROOT_DIR/Dockerfile"
    --platform "$PLATFORMS"
    --build-arg "APP_JAR=src/mcpagent/target/$JAR_NAME"
    --build-arg "BASE_IMAGE=admingentoro/gentoro:base-$VERSION"
    -t "admingentoro/gentoro:$VERSION"
    -t "admingentoro/gentoro:latest"
    --push
  )
else
  echo "Building for local use"
  # For local builds, use docker buildx build --load to handle multi-platform correctly
  # Don't override PLATFORMS if it was set via --platform parameter
  if [[ "$PLATFORM_FLAG" != "--platform" ]]; then
    PLATFORMS="linux/amd64"
  fi
  
  # Check if base image exists locally - try both versioned and latest
  BASE_IMAGE_NAME="admingentoro/gentoro:base-$VERSION"
  if ! docker image inspect "$BASE_IMAGE_NAME" >/dev/null 2>&1; then
    echo "⚠️  Base image $BASE_IMAGE_NAME not found, trying base-latest..."
    if docker image inspect "admingentoro/gentoro:base-latest" >/dev/null 2>&1; then
      BASE_IMAGE_NAME="admingentoro/gentoro:base-latest"
      echo "✅ Using admingentoro/gentoro:base-latest"
    else
      echo "❌ No base image found locally"
      echo "Available base images:"
      docker images | grep "admingentoro/gentoro.*base" || echo "No base images found"
      exit 1
    fi
  fi
  
  # For local builds, use docker buildx build --load for cross-platform support
  # Use docker build only for native AMD64 builds
  if [[ "$PLATFORMS" == "linux/amd64" ]]; then
    echo "Native AMD64 build, using docker build for local-only"
    BUILD_CMD="docker build"
    BUILD_ARGS=(
      -f "$ROOT_DIR/Dockerfile"
      --build-arg "APP_JAR=src/mcpagent/target/$JAR_NAME"
      --build-arg "BASE_IMAGE=$BASE_IMAGE_NAME"
      -t "admingentoro/gentoro:$VERSION"
      -t "admingentoro/gentoro:latest"
    )
  else
    echo "Cross-platform build detected, using buildx"
    BUILD_CMD="docker buildx build"
    BUILD_ARGS=(
      -f "$ROOT_DIR/Dockerfile"
      --platform "$PLATFORMS"
      --build-arg "APP_JAR=src/mcpagent/target/$JAR_NAME"
      --build-arg "BASE_IMAGE=$BASE_IMAGE_NAME"
      -t "admingentoro/gentoro:$VERSION"
      -t "admingentoro/gentoro:latest"
      --load
    )
  fi
fi

# Build Docker image
if ! $BUILD_CMD "${BUILD_ARGS[@]}" "$ROOT_DIR"; then
  echo "Failed to build product image"
  exit 1
fi

echo "Successfully built admingentoro/gentoro:$VERSION for platforms: $PLATFORMS"
