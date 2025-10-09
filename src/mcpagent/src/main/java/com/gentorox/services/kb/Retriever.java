package com.gentorox.services.kb;

import com.gentorox.services.kb.model.Chunk;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Retriever {
  private final IndexerService indexer;
  public Retriever(IndexerService indexer) { this.indexer = indexer; }
  public List<Chunk> query(String q, int topK) { return indexer.getKb().search(q, topK); }
}
