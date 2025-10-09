package com.gentorox.tools;

import com.gentorox.core.api.ToolSpec;
import com.gentorox.services.kb.ContextBuilder;
import com.gentorox.services.kb.Retriever;
import com.gentorox.services.telemetry.TelemetryService;
import com.gentorox.services.telemetry.TelemetrySession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RetrieveContextTool implements NativeTool {
  private final Retriever retriever;
  private final ContextBuilder builder;
  private final TelemetryService telemetry;

  public RetrieveContextTool(Retriever retriever, ContextBuilder builder, TelemetryService telemetry) {
    this.retriever = retriever; this.builder = builder; this.telemetry = telemetry;
  }

  @Override
  public ToolSpec spec() {
    return new ToolSpec("retrieveContext","Retrieve contextual information from the knowledge base using a natural language query.",
        java.util.List.of(new ToolSpec.Parameter("query","string",true,"Natural language query",null),
            new ToolSpec.Parameter("topK","integer",false,"Max results (default 4)",null)));
  }

  @Override
  public String execute(Map<String, Object> args) {
    String q = String.valueOf(args.getOrDefault("query",""));
    int topK = ((Number) args.getOrDefault("topK", 4)).intValue();
    var session = TelemetrySession.create();
    return telemetry.inSpan(session,"tool.execute", java.util.Map.of("tool","retrieveContext"),
        () -> {
          var res = telemetry.inSpan(session,"kb.retrieve", java.util.Map.of(), () -> retriever.query(q, topK));
          return builder.buildPromptBlocks(res);
        });
  }
}
