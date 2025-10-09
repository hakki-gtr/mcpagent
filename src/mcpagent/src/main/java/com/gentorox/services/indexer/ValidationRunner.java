package com.gentorox.services.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public final class ValidationRunner {
  private static final Logger log = LoggerFactory.getLogger(ValidationRunner.class);
  private ValidationRunner() {}
  public static void run() {
    String foundation = System.getenv().getOrDefault("FOUNDATION_DIR", "/var/foundation");
    File root = new File(foundation);
    if (!root.exists()) { log.error("Foundation dir not found: {}", root.getAbsolutePath()); return; }
    File agent = new File(root, "Agent.md");
    if (!agent.exists() || agent.length() == 0) log.error("Agent.md is missing or empty at {}", agent.getAbsolutePath());
    else log.info("✓ Agent.md found");
    File apis = new File(root, "apis");
    boolean hasOpenApi = apis.exists() && apis.isDirectory()
        && Arrays.stream(apis.listFiles((d, n) -> n.endsWith(".yaml") || n.endsWith(".yml") || n.endsWith(".json"))).findFirst().isPresent();
    if (!hasOpenApi) log.warn("No OpenAPI spec found under {}/apis; stubs will be unavailable", root.getAbsolutePath());
    else log.info("✓ OpenAPI spec detected");
    File docs = new File(root, "docs");
    if (docs.exists() && docs.isDirectory()) {
      long count = Arrays.stream(docs.listFiles((d, n) -> n.endsWith(".md") || n.endsWith(".mdx"))).count();
      log.info("✓ Docs found: {} file(s)", count);
    } else log.info("ℹ No docs folder found (optional)");
    String otlp = System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT");
    File localOtelCfg = new File("/etc/otel-collector-config.yaml");
    if (otlp != null && !otlp.isBlank()) log.info("✓ OTLP endpoint set: {}", otlp);
    else if (localOtelCfg.exists() && readable(localOtelCfg)) log.info("✓ Local OTEL collector config present at {}", localOtelCfg.getAbsolutePath());
    else log.warn("No OTLP endpoint or local collector config detected; telemetry will default to local logging.");
    log.info("MCP server expected at /mcp (http), server.port controls port (default 8080).");
  }
  private static boolean readable(File f) { try { return Files.isReadable(f.toPath()); } catch (Exception e) { return false; } }
}
