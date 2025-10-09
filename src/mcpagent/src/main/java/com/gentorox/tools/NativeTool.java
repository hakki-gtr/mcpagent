package com.gentorox.tools;

import com.gentorox.core.api.ToolSpec;
import java.util.Map;

public interface NativeTool {
  ToolSpec spec();
  String execute(Map<String, Object> args);
}
