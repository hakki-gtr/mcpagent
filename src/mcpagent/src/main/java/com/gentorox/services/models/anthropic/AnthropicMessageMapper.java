package com.gentorox.services.models.anthropic;

import com.anthropic.models.MessagesCreateParams;
import com.anthropic.types.ContentBlock;
import com.gentorox.core.model.InferenceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AnthropicMessageMapper {
  private AnthropicMessageMapper() {}
  public static List<MessagesCreateParams.Message> map(List<InferenceRequest.Message> messages) {
    List<MessagesCreateParams.Message> out = new ArrayList<>();
    for (var m : messages) {
      String text = Optional.ofNullable(m.content()).map(Object::toString).orElse("");
      String role = switch (m.role()) { case "assistant" -> "assistant"; case "system" -> "system"; default -> "user"; };
      out.add(MessagesCreateParams.Message.builder().role(role).addContent(ContentBlock.text(text)).build());
    }
    return out;
  }
  public static Optional<com.gentorox.core.model.InferenceResponse.ToolCall> firstToolCall(com.anthropic.models.MessageResponse resp) {
    if (resp.content() == null) return Optional.empty();
    return resp.content().stream().filter(b -> b.toolUse() != null).findFirst()
        .map(b -> new com.gentorox.core.model.InferenceResponse.ToolCall(b.toolUse().name(), b.toolUse().input()));
  }
}
