package com.gentorox.services.kb;

import com.gentorox.services.kb.model.Chunk;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MarkdownChunker {
  private MarkdownChunker() {}
  public static List<Chunk> chunk(String docId, String text) {
    List<Chunk> out = new ArrayList<>();
    String[] parts = text.split("\n(?=# )");
    int pos = 0;
    for (String p : parts) {
      for (String window : windows(p, 800)) {
        out.add(new Chunk(docId, docId + ":" + UUID.randomUUID(), window, pos++));
      }
    }
    return out;
  }
  private static List<String> windows(String s, int size) {
    List<String> res = new ArrayList<>(); int i = 0;
    while (i < s.length()) { int end = Math.min(i + size, s.length()); res.add(s.substring(i, end)); i = end; }
    return res;
  }
}
