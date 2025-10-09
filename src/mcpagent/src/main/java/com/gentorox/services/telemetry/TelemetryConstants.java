package com.gentorox.services.telemetry;

public final class TelemetryConstants {
  private TelemetryConstants() {}
  public static final String TRACER = "com.gentorox.mcpagent";
  public static final String METER  = "com.gentorox.mcpagent";

  public static final String ATTR_SESSION_ID = "gentoro.session.id";
  public static final String ATTR_PROVIDER   = "gentoro.model.provider";
  public static final String ATTR_MODEL      = "gentoro.model.name";
  public static final String ATTR_TOOL       = "gentoro.tool.name";
}
