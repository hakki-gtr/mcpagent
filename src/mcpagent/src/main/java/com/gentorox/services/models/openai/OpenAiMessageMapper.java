package com.gentorox.services.models.openai;

import com.gentorox.core.model.InferenceRequest;
import com.openai.models.*;
import java.util.ArrayList;
import java.util.List;

public final class OpenAiMessageMapper {
  private OpenAiMessageMapper() {}
  public static List<ChatCompletionMessageParam> map(List<InferenceRequest.Message> messages) {
    List<ChatCompletionMessageParam> out = new ArrayList<>();
    for (var m : messages) {
      String role = m.role();
      String text = m.content() == null ? "" : String.valueOf(m.content());
      switch (role) {
        case "system" -> out.add(ChatCompletionSystemMessageParam.builder().content(text).build());
        case "assistant" -> out.add(ChatCompletionAssistantMessageParam.builder()
            .content(List.of(ChatCompletionMessageParamAssistantContent.builder().text(text).build())).build());
        case "tool" -> out.add(ChatCompletionToolMessageParam.builder().content(text).toolCallId("tool-call").build());
        default -> out.add(ChatCompletionUserMessageParam.builder()
            .content(List.of(ChatCompletionMessageParamUserContent.builder().text(text).build())).build());
      }
    }
    return out;
  }
}
