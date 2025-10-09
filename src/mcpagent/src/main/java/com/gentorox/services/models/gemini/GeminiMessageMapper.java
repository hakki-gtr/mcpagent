package com.gentorox.services.models.gemini;

import com.gentorox.core.model.InferenceRequest;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GeminiMessageMapper {
  private GeminiMessageMapper() {}
  public static List<Content> map(List<InferenceRequest.Message> messages) {
    List<Content> out = new ArrayList<>();
    for (var m : messages) {
      String role = switch (m.role()) { case "assistant" -> "model"; case "system" -> "system"; default -> "user"; };
      String text = Optional.ofNullable(m.content()).map(Object::toString).orElse("");
      out.add(Content.builder().role(role).parts(List.of(Part.fromText(text))).build());
    }
    return out;
  }
  public static String flattenText(com.google.genai.types.GenerateContentResponse resp) {
    if (resp.getCandidates() == null || resp.getCandidates().isEmpty()) return "";
    var cand = resp.getCandidates().get(0);
    StringBuilder sb = new StringBuilder();
    cand.getContent().getParts().forEach(p -> { if (p.getText() != null) sb.append(p.getText()); });
    return sb.toString();
  }
  public static java.util.Optional<com.gentorox.core.model.InferenceResponse.ToolCall> firstToolCall(
      com.google.genai.types.GenerateContentResponse resp) { return Optional.empty(); }
}
