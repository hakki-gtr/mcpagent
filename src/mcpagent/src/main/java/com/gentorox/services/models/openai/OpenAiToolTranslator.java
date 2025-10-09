package com.gentorox.services.models.openai;

import com.gentorox.core.api.ToolSpec;
import com.gentorox.services.models.ToolTranslator;
import com.openai.models.ChatCompletionToolParam;
import com.openai.models.FunctionObject;
import java.util.*;

public class OpenAiToolTranslator implements ToolTranslator<ChatCompletionToolParam> {
  @Override
  public List<ChatCompletionToolParam> translate(List<ToolSpec> tools) {
    List<ChatCompletionToolParam> out = new ArrayList<>();
    for (ToolSpec t : tools) {
      Map<String, Object> jsonSchema = com.gentorox.services.models.JsonSchemaBuilder.from(t);
      out.add(ChatCompletionToolParam.builder()
          .type(ChatCompletionToolParam.Type.FUNCTION)
          .function(FunctionObject.builder().name(t.name()).description(t.description()).parameters(jsonSchema).build())
          .build());
    }
    return out;
  }
}
