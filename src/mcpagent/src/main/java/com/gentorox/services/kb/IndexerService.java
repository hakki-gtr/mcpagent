package com.gentorox.services.kb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentorox.services.kb.model.Document;
import com.gentorox.services.kb.model.Source;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

@Service
public class IndexerService {
  private final KnowledgeBase kb;
  private final ObjectMapper om = new ObjectMapper();
  public IndexerService() { this.kb = new InMemoryKnowledgeBase(); }
  public KnowledgeBase getKb() { return kb; }

  public void ingestFoundation(String rootDir) {
    File root = new File(rootDir);
    if (!root.exists()) return;
    File agent = new File(root, "Agent.md");
    if (agent.exists()) addDoc("agent-md", "Agent Prompt", read(agent), Source.AGENT_MD, Map.of("path", agent.getAbsolutePath()));
    File docs = new File(root, "docs");
    if (docs.exists() && docs.isDirectory()) {
      for (File f : Optional.ofNullable(docs.listFiles((d,n) -> n.endsWith(".md") || n.endsWith(".mdx"))).orElse(new File[0])) {
        addDoc(UUID.randomUUID().toString(), f.getName(), read(f), Source.MARKDOWN_DOC, Map.of("path", f.getAbsolutePath()));
      }
    }
    File apis = new File(root, "apis");
    if (apis.exists() && apis.isDirectory()) {
      for (File f : Optional.ofNullable(apis.listFiles((d,n) -> n.endsWith(".json"))).orElse(new File[0])) addOpenApiJson(f);
    }
  }

  private void addDoc(String id, String title, String text, Source src, Map<String,String> meta) {
    var doc = new Document(id, title, text, src, meta, Instant.now());
    kb.add(doc, MarkdownChunker.chunk(id, text));
  }

  private void addOpenApiJson(File jsonSpec) {
    try {
      JsonNode root = om.readTree(jsonSpec);
      String title = root.path("info").path("title").asText("API");
      JsonNode paths = root.path("paths");
      Iterator<String> it = paths.fieldNames();
      while (it.hasNext()) {
        String p = it.next();
        JsonNode methods = paths.get(p);
        methods.fieldNames().forEachRemaining(m -> {
          JsonNode op = methods.get(m);
          String opId = op.path("operationId").asText(m + "_" + p);
          String summary = op.path("summary").asText("");
          String desc = op.path("description").asText("");
          String text = "# " + title + " – " + opId + "\n" +
              "**Method**: " + m.toUpperCase() + " " + p + "\n\n" +
              "**Summary**: " + summary + "\n\n" + desc + "\n";
          addDoc("api:" + opId, opId, text, Source.GENERATED_API_METHOD, Map.of("path", p, "method", m));
        });
      }
    } catch (Exception ignored) { }
  }

  private static String read(File f) { try { return Files.readString(f.toPath()); } catch (Exception e) { return ""; } }
}
