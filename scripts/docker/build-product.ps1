Param([string]$Version="dev")
$ROOT = (Resolve-Path "$PSScriptRoot\..\..").Path
Push-Location "$ROOT\product"; if (Test-Path .\mvnw) { ./mvnw -q -DskipTests package } else { mvn -q -DskipTests package }; Pop-Location
docker build -f "$ROOT\Dockerfile" --build-arg APP_JAR=product/target/mcpagent-0.1.0-SNAPSHOT.jar --build-arg BASE_IMAGE=admingentoro/base:latest -t "admingentoro/mcpagent:$Version" -t "admingentoro/mcpagent:latest" "$ROOT"
