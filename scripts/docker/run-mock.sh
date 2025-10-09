#!/usr/bin/env bash
set -euo pipefail
# Placeholder for a mock server (Node/Java/etc.). If not present, idle.
if [[ -f "/opt/mock-server/server.js" ]]; then
  echo "[run-mock] starting mock server on :8082"
  exec node /opt/mock-server/server.js
else
  echo "[run-mock] no mock server found, idling"
  while true; do sleep 3600; done
fi
