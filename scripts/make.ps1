Param(
  [Parameter(Mandatory=$true)][string]$Command,
  [string]$Arg1
)

$ErrorActionPreference = "Stop"
$ROOT = (Resolve-Path "$PSScriptRoot\..").Path
$SCRIPTS = Join-Path $ROOT "scripts"
$DOCKER = Join-Path $SCRIPTS "docker"
$OPENAPI = Join-Path $SCRIPTS "openapi"

switch ($Command) {
  "build-base"   { & "$DOCKER\build-base.ps1" ($Arg1 -ne $null ? $Arg1 : "latest") }
  "build-product"{ & "$DOCKER\build-product.ps1" ($Arg1 -ne $null ? $Arg1 : "dev") }
  "publish"      { & "$DOCKER\publish.ps1" ($Arg1 -ne $null ? $Arg1 : "dev") }
  "openapi"      { & "$OPENAPI\generate-ts.ps1" ($Arg1) }
  "release"      { & "$SCRIPTS\release.ps1" ($Arg1 -ne $null ? $Arg1 : "patch") }
  "test"         { Push-Location "$ROOT\product"; if (Test-Path .\mvnw) { ./mvnw -q -DskipTests=false test } else { mvn -q -DskipTests=false test }; Pop-Location }
  "run"          { Push-Location "$ROOT\product"; if (Test-Path .\mvnw) { ./mvnw -q spring-boot:run } else { mvn -q spring-boot:run }; Pop-Location }
  "validate"     { Push-Location "$ROOT\product"; if (Test-Path .\mvnw) { ./mvnw -q -DskipTests package } else { mvn -q -DskipTests package }; Pop-Location;
                   & java -jar "$ROOT\product\target\mcpagent-0.1.0-SNAPSHOT.jar" --process=validate }
  default        { Write-Host "Unknown command: $Command"; exit 1 }
}
