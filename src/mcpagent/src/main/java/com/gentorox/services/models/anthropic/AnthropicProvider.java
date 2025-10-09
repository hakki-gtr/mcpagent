package com.gentorox.services.models.anthropic;

import com.anthropic.AnthropicClient;
import com.anthropic.models.MessagesCreateParams;
import com.gentorox.core.api.ModelProvider;
import com.gentorox.core.api.ToolSpec;
import com.gentorox.core.model.InferenceRequest;
import com.gentorox.core.model.InferenceResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AnthropicProvider implements ModelProvider {
  private final AnthropicClient client;
  private final AnthropicToolTranslator translator = new AnthropicToolTranslator();

  public AnthropicProvider() {
    this.client = AnthropicClient.builder().apiKey(System.getenv("ANTHROPIC_API_KEY")).build();
  }

  @Override public String id() { return "anthropic"; }

  @Override
  public InferenceResponse infer(InferenceRequest req, List<ToolSpec> tools) {
    var toolDefs = translator.translate(tools);
    var params = MessagesCreateParams.builder()
        .model(req.model()).messages(AnthropicMessageMapper.map(req.messages())).tools(toolDefs)
        .temperature((Double) req.options().getOrDefault("temperature", 0.2)).build();
    var result = client.messages().create(params);
    String text = result.content().stream().filter(c -> c.text() != null).map(com.anthropic.types.ContentBlock::text).reduce("", (a,b)->a+b);
    var call = AnthropicMessageMapper.firstToolCall(result);
    return new InferenceResponse(text, call, result.id());
  }
}
