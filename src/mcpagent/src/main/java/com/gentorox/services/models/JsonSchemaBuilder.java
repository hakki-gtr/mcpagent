package com.gentorox.services.models;

import com.gentorox.core.api.ToolSpec;
import java.util.*;

public final class JsonSchemaBuilder {
  private JsonSchemaBuilder() {}
  public static Map<String, Object> from(ToolSpec spec) {
    Map<String, Object> root = new LinkedHashMap<>();
    root.put("type", "object");
    Map<String, Object> props = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();
    for (ToolSpec.Parameter p : spec.parameters()) {
      Map<String, Object> pSchema = new LinkedHashMap<>();
      pSchema.put("type", p.type());
      if (p.description() != null) pSchema.put("description", p.description());
      if (p.schemaExtras() != null) pSchema.putAll(p.schemaExtras());
      props.put(p.name(), pSchema);
      if (p.required()) required.add(p.name());
    }
    root.put("properties", props);
    if (!required.isEmpty()) root.put("required", required);
    root.put("$schema", "http://json-schema.org/draft-07/schema#");
    return root;
  }
}
