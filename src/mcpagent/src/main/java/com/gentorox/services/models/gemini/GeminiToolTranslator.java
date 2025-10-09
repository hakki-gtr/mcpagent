package com.gentorox.services.models.gemini;

import com.gentorox.core.api.ToolSpec;
import com.gentorox.services.models.ToolTranslator;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeminiToolTranslator implements ToolTranslator<Tool> {
  @Override
  public List<Tool> translate(List<ToolSpec> tools) {
    List<Tool> out = new ArrayList<>();
    for (ToolSpec t : tools) {
      Map<String, Object> schema = com.gentorox.services.models.JsonSchemaBuilder.from(t);
      var fn = FunctionDeclaration.builder().name(t.name()).description(t.description()).parameters(schema).build();
      out.add(Tool.builder().functionDeclarations(java.util.List.of(fn)).build());
    }
    return out;
  }
}
