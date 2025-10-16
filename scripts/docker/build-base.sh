#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:-latest}"
PUSH_FLAG="${2:-}"
PLATFORM_FLAG="${3:-}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DOCKERFILE="$ROOT_DIR/Dockerfile.base"

# Default platforms for multi-arch builds
PLATFORMS="${DOCKER_PLATFORMS:-linux/amd64,linux/arm64}"

# Handle platform flag
if [[ "$PLATFORM_FLAG" == "--platform" && -n "${4:-}" ]]; then
  PLATFORMS="${4}"
fi

# Validate Dockerfile exists
if [[ ! -f "$DOCKERFILE" ]]; then
  echo "Dockerfile not found: $DOCKERFILE"
  exit 1
fi

echo "Building base image with version: $VERSION"
echo "Dockerfile: $DOCKERFILE"
echo "Platforms: $PLATFORMS"

# Determine build command based on whether we're pushing
if [[ "$PUSH_FLAG" == "--push" ]]; then
  echo "Will push images to registry"
  BUILD_CMD="docker buildx build"
  BUILD_ARGS=(
    -f "$DOCKERFILE"
    --platform "$PLATFORMS"
    -t "admingentoro/gentoro:base-$VERSION"
    -t "admingentoro/gentoro:base-latest"
    --push
  )
else
  echo "Building for local use (load to docker)"
  # For local builds, we can only load one platform
  # Don't override PLATFORMS if it was set via --platform parameter
  if [[ "$PLATFORM_FLAG" != "--platform" ]]; then
    PLATFORMS="linux/amd64"
  fi
  BUILD_CMD="docker buildx build"
  BUILD_ARGS=(
    -f "$DOCKERFILE"
    --platform "$PLATFORMS"
    -t "admingentoro/gentoro:base-$VERSION"
    -t "admingentoro/gentoro:base-latest"
    --load
  )
fi

# Build Docker image
if ! $BUILD_CMD "${BUILD_ARGS[@]}" "$ROOT_DIR"; then
  echo "Failed to build base image"
  exit 1
fi

echo "Successfully built admingentoro/gentoro:base-$VERSION for platforms: $PLATFORMS"
