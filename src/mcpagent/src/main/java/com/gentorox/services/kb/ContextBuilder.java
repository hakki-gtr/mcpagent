package com.gentorox.services.kb;

import com.gentorox.services.kb.model.Chunk;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContextBuilder {
  public String buildPromptBlocks(List<Chunk> chunks) {
    String header = "### Retrieved Context (do not quote verbatim unless necessary)\n";
    String body = chunks.stream().map(c -> "- " + c.text().replace("\n", " ").trim()).collect(Collectors.joining("\n"));
    return header + body + "\n";
  }
}
