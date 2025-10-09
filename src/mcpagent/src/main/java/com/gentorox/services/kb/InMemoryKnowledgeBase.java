package com.gentorox.services.kb;

import com.gentorox.services.kb.model.Chunk;
import com.gentorox.services.kb.model.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryKnowledgeBase implements KnowledgeBase {
  private final Map<String, Document> docs = new ConcurrentHashMap<>();
  private final Map<String, List<Chunk>> chunksByDoc = new ConcurrentHashMap<>();
  private final Map<String, Map<String, Integer>> termFreqByChunk = new ConcurrentHashMap<>();
  private final Map<String, Integer> df = new ConcurrentHashMap<>();
  private int totalChunks = 0;

  @Override
  public String add(Document doc, List<Chunk> chunks) {
    docs.put(doc.id(), doc);
    chunksByDoc.put(doc.id(), chunks);
    for (Chunk c : chunks) {
      totalChunks++;
      Map<String,Integer> tf = tokenize(c.text()).stream().collect(Collectors.toMap(t -> t, t -> 1, Integer::sum));
      termFreqByChunk.put(c.chunkId(), tf);
      tf.keySet().forEach(t -> df.merge(t, 1, Integer::sum));
    }
    return doc.id();
  }

  @Override public Optional<Document> get(String docId) { return Optional.ofNullable(docs.get(docId)); }

  @Override
  public List<Chunk> search(String query, int topK) {
    var qTerms = tokenize(query);
    Map<String, Double> scores = new HashMap<>();
    for (var e : termFreqByChunk.entrySet()) {
      String chunkId = e.getKey();
      Map<String,Integer> tf = e.getValue();
      double score = 0.0;
      for (String t : qTerms) {
        int f = tf.getOrDefault(t, 0);
        if (f == 0) continue;
        int dfi = df.getOrDefault(t, 1);
        double idf = Math.log((1.0 + totalChunks) / dfi);
        score += f * idf;
      }
      if (score > 0) scores.put(chunkId, score);
    }
    return scores.entrySet().stream()
        .sorted(Map.Entry.<String,Double>comparingByValue().reversed())
        .limit(topK)
        .map(e -> findChunk(e.getKey()))
        .filter(Objects::nonNull)
        .toList();
  }

  private Chunk findChunk(String chunkId) {
    for (var list : chunksByDoc.values()) for (var c : list) if (c.chunkId().equals(chunkId)) return c;
    return null;
  }

  @Override public void clear() { docs.clear(); chunksByDoc.clear(); termFreqByChunk.clear(); df.clear(); totalChunks = 0; }
  @Override public int size() { return docs.size(); }

  private static List<String> tokenize(String s) {
    return Arrays.stream(s.toLowerCase().split("[^a-z0-9]+")).filter(w -> !w.isBlank()).toList();
  }
}
