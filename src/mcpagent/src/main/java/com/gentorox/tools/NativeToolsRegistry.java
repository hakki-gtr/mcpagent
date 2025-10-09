package com.gentorox.tools;

import com.gentorox.core.api.ToolSpec;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class NativeToolsRegistry {
  private final List<NativeTool> tools;
  public NativeToolsRegistry(List<NativeTool> tools) { this.tools = tools; }

  public List<ToolSpec> currentToolSpecs() { return tools.stream().map(NativeTool::spec).toList(); }

  public String execute(String name, Map<String, Object> args) {
    return tools.stream().filter(t -> t.spec().name().equals(name)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown tool: " + name)).execute(args);
  }
}
