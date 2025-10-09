package com.gentorox.services.models.gemini;

import com.gentorox.core.api.ModelProvider;
import com.gentorox.core.api.ToolSpec;
import com.gentorox.core.model.InferenceRequest;
import com.gentorox.core.model.InferenceResponse;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Content;
import com.google.genai.types.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GeminiProvider implements ModelProvider {
  private final Client client;
  private final GeminiToolTranslator translator = new GeminiToolTranslator();

  public GeminiProvider() {
    this.client = Client.builder().apiKey(System.getenv("GEMINI_API_KEY")).build();
  }

  @Override public String id() { return "gemini"; }

  @Override
  public InferenceResponse infer(InferenceRequest req, List<ToolSpec> tools) {
    var toolDefs = translator.translate(tools);
    var cfg = GenerateContentConfig.builder().model(req.model()).tools(toolDefs)
        .temperature((Double) req.options().getOrDefault("temperature", 0.2)).build();
    List<Content> contents = GeminiMessageMapper.map(req.messages());
    var resp = client.models().generateContent(cfg, contents);
    String text = GeminiMessageMapper.flattenText(resp);
    return new InferenceResponse(text, Optional.empty(), resp.getResponseId());
  }
}
