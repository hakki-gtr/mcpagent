package com.gentorox.controllers;

import io.modelcontextprotocol.spring.server.McpServerConfigurer;
import io.modelcontextprotocol.spring.server.McpServerRegistry;
import io.modelcontextprotocol.core.transport.httpstream.HttpStreamServerTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {
  @Bean
  McpServerConfigurer serverConfigurer() {
    return new McpServerConfigurer() {
      @Override
      public void configure(McpServerRegistry registry) {
        registry.transport(HttpStreamServerTransport.builder().path("/mcp").build());
      }
    };
  }
}
