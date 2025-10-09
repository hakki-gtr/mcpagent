package com.gentorox.services.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

public final class MockServerRunner {
  private static final Logger log = LoggerFactory.getLogger(MockServerRunner.class);
  private MockServerRunner() {}
  public static void run(ApplicationArguments args) {
    String port = args.containsOption("tcp-port") ? args.getOptionValues("tcp-port").get(0) : "8082";
    log.info("Mock server mode requested on port {}. This stub assumes the external mock server container.", port);
  }
}
