package com.gentorox.services.typescript;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TypescriptRuntimeClient {
  private final WebClient web;
  public TypescriptRuntimeClient() {
    this.web = WebClient.builder().baseUrl(System.getenv().getOrDefault("TS_RUNTIME_URL","http://localhost:7070")).build();
  }
  public Mono<ExecResult> exec(String code, Object args) {
    return web.post().uri("/exec").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new ExecRequest(code, args)).retrieve().bodyToMono(ExecResult.class);
  }
  public record ExecRequest(String code, Object args) {}
  public record ExecResult(String stdout, String stderr, Object result) {}
}
