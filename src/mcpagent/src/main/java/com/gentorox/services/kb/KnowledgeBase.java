package com.gentorox.services.kb;

import com.gentorox.services.kb.model.Chunk;
import com.gentorox.services.kb.model.Document;
import java.util.List;
import java.util.Optional;

public interface KnowledgeBase {
  String add(Document doc, List<Chunk> chunks);
  Optional<Document> get(String docId);
  List<Chunk> search(String query, int topK);
  void clear();
  int size();
}
