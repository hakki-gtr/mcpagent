#!/usr/bin/env bash
set -euo pipefail

# This entrypoint starts supervisord, which orchestrates:
# - OpenTelemetry Collector (otelcol)
# - Java Spring Boot app
# - (optional) TS runtime & mock server if present

echo "[entrypoint] starting supervisord..."
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
