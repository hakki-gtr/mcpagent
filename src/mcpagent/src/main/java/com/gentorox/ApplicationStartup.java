package com.gentorox;

import com.gentorox.services.indexer.MockServerRunner;
import com.gentorox.services.indexer.ValidationRunner;
import com.gentorox.services.testsuite.TestSuiteRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationRunner {
  @Override
  public void run(ApplicationArguments args) {
    String process = args.containsOption("process") ? args.getOptionValues("process").get(0) : "standard";
    switch (process) {
      case "validate" -> ValidationRunner.run();
      case "test_suite" -> TestSuiteRunner.run();
      case "mock-server" -> MockServerRunner.run(args);
      default -> System.out.println("Starting standard mode (MCP server + orchestrator)");
    }
  }
}
