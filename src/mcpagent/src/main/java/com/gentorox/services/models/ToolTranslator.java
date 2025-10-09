package com.gentorox.services.models;

import com.gentorox.core.api.ToolSpec;
import java.util.List;

public interface ToolTranslator<P> {
  List<P> translate(List<ToolSpec> tools);
}
