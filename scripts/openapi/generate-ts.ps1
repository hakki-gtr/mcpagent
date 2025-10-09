Param([string]$Spec)
if (-not $Spec) { $Spec = "$env:FOUNDATION_DIR\apis\openapi.yaml" }
$ROOT = (Resolve-Path "$PSScriptRoot\..\..").Path
$OUT = "$ROOT\product\src\main\resources\typescript-runtime\generated"
New-Item -ItemType Directory -Force -Path $OUT | Out-Null
if (Get-Command openapi-generator -ErrorAction SilentlyContinue) {
  openapi-generator generate -i $Spec -g typescript-axios -o $OUT --skip-validate-spec
} else {
  docker run --rm -v "$ROOT:/work" -w /work openapitools/openapi-generator-cli:v7.9.0 generate -i $Spec -g typescript-axios -o "product/src/main/resources/typescript-runtime/generated" --skip-validate-spec
}
