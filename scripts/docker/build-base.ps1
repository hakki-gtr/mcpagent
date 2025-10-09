Param([string]$Version="latest")
$ROOT = (Resolve-Path "$PSScriptRoot\..\..").Path
docker build -f "$ROOT\Dockerfile.base" -t "admingentoro/base:$Version" -t "admingentoro/base:latest" "$ROOT"
