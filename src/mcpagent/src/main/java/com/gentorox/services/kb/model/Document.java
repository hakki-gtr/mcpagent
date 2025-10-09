package com.gentorox.services.kb.model;

import java.time.Instant;
import java.util.Map;

public record Document(String id, String title, String text, Source source, Map<String, String> metadata, Instant createdAt) {}
