package com.gentorox.core.api;

import java.util.List;
import java.util.Map;

public record ToolSpec(String name, String description, List<Parameter> parameters) {
  public record Parameter(String name, String type, boolean required, String description, Map<String, Object> schemaExtras) {}
}
