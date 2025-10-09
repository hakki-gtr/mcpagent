package com.gentorox.services.models.anthropic;

import com.anthropic.types.Tool;
import com.gentorox.core.api.ToolSpec;
import com.gentorox.services.models.ToolTranslator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnthropicToolTranslator implements ToolTranslator<Tool> {
  @Override
  public List<Tool> translate(List<ToolSpec> tools) {
    List<Tool> out = new ArrayList<>();
    for (ToolSpec t : tools) {
      Map<String, Object> schema = com.gentorox.services.models.JsonSchemaBuilder.from(t);
      out.add(Tool.builder().name(t.name()).description(t.description()).inputSchema(schema).build());
    }
    return out;
  }
}
