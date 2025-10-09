Param([string]$Version="dev")
docker push "admingentoro/base:$Version"; docker push "admingentoro/base:latest" 2>$null
docker push "admingentoro/mcpagent:$Version"; docker push "admingentoro/mcpagent:latest" 2>$null
