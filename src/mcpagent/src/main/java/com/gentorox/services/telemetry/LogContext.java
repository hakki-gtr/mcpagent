package com.gentorox.services.telemetry;

import org.slf4j.MDC;

public final class LogContext implements AutoCloseable {
  public LogContext(TelemetrySession s) { if (s != null) MDC.put("sessionId", s.id()); }
  @Override public void close() { MDC.remove("sessionId"); }
}
