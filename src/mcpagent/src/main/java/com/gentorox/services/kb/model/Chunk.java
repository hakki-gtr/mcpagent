package com.gentorox.services.kb.model;

public record Chunk(String docId, String chunkId, String text, int position) {}
