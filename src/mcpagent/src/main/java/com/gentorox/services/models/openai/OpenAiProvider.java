package com.gentorox.services.models.openai;

import com.gentorox.core.api.ModelProvider;
import com.gentorox.core.api.ToolSpec;
import com.gentorox.core.model.InferenceRequest;
import com.gentorox.core.model.InferenceResponse;
import com.openai.OpenAI;
import com.openai.core.http.ApacheHttpClient;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionMessageToolCall;
import com.openai.models.ChatCompletionToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OpenAiProvider implements ModelProvider {
  private final OpenAI client;
  private final OpenAiToolTranslator translator = new OpenAiToolTranslator();

  public OpenAiProvider() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    String baseUrl = System.getenv().getOrDefault("OPENAI_BASE_URL", OpenAI.DEFAULT_BASE_URL);
    this.client = OpenAI.builder().apiKey(apiKey).baseUrl(baseUrl).httpClient(ApacheHttpClient.create()).build();
  }

  @Override public String id() { return "openai"; }

  @Override
  public InferenceResponse infer(InferenceRequest req, List<ToolSpec> tools) {
    List<ChatCompletionMessageParam> messages = OpenAiMessageMapper.map(req.messages());
    List<ChatCompletionToolParam> toolDefs = translator.translate(tools);

    var params = ChatCompletionCreateParams.builder()
        .model(req.model()).messages(messages).tools(toolDefs)
        .temperature((Double) req.options().getOrDefault("temperature", 0.2))
        .build();

    var completion = client.chatCompletions().create(params);
    var choice = completion.choices().get(0);
    var msg = choice.message();

    Optional<InferenceResponse.ToolCall> toolCall = Optional.empty();
    List<ChatCompletionMessageToolCall> calls = msg.toolCalls();
    if (calls != null && !calls.isEmpty()) {
      var c = calls.get(0);
      toolCall = Optional.of(new InferenceResponse.ToolCall(c.function().name(), c.function().arguments()));
    }

    String content = msg.content() == null ? "" : msg.content().stream().map(p -> p.text().orElse("")).reduce("", (a,b)->a+b);
    return new InferenceResponse(content, toolCall, completion.id());
  }
}
