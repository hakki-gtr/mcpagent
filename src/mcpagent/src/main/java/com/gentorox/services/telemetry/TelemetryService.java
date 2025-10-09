package com.gentorox.services.telemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

import static com.gentorox.services.telemetry.TelemetryConstants.*;

@Component
public class TelemetryService {
  private final Tracer tracer = GlobalOpenTelemetry.getTracer(TRACER);
  private final Meter meter   = GlobalOpenTelemetry.meterBuilder(METER).build();

  private final LongCounter promptsTotal =
      meter.counterBuilder("gentoro.prompts.total").setDescription("Total prompts received").build();
  private final LongCounter toolCallsTotal =
      meter.counterBuilder("gentoro.tool.calls.total").setDescription("Tool calls executed").build();
  private final LongCounter modelCallsTotal =
      meter.counterBuilder("gentoro.model.calls.total").setDescription("Model calls executed").build();

  public <T> T runRoot(TelemetrySession session, String name, Map<String,String> attrs, Supplier<T> body) {
    var spanBuilder = tracer.spanBuilder(name).setSpanKind(SpanKind.SERVER);
    if (session != null) spanBuilder.setAttribute(ATTR_SESSION_ID, session.id());
    if (attrs != null) attrs.forEach((k,v) -> spanBuilder.setAttribute(AttributeKey.stringKey(k), v));
    Span span = spanBuilder.startSpan();
    try (Scope ignore = span.makeCurrent()) { return body.get(); }
    catch (RuntimeException e) { span.recordException(e); span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR); throw e; }
    finally { span.end(); }
  }

  public <T> T inSpan(TelemetrySession session, String name, Map<String,String> attrs, Supplier<T> body) {
    var spanBuilder = tracer.spanBuilder(name).setSpanKind(SpanKind.INTERNAL);
    if (session != null) spanBuilder.setAttribute(ATTR_SESSION_ID, session.id());
    if (attrs != null) attrs.forEach((k,v) -> spanBuilder.setAttribute(AttributeKey.stringKey(k), v));
    Span span = spanBuilder.startSpan();
    try (Scope ignore = span.makeCurrent()) { return body.get(); }
    catch (RuntimeException e) { span.recordException(e); span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR); throw e; }
    finally { span.end(); }
  }

  public void countPrompt(TelemetrySession s, String provider, String model) {
    promptsTotal.add(1, Attributes.of(
        AttributeKey.stringKey(ATTR_SESSION_ID), s.id(),
        AttributeKey.stringKey(ATTR_PROVIDER), provider,
        AttributeKey.stringKey(ATTR_MODEL), model));
  }

  public void countTool(TelemetrySession s, String toolName) {
    toolCallsTotal.add(1, Attributes.of(
        AttributeKey.stringKey(ATTR_SESSION_ID), s.id(),
        AttributeKey.stringKey(ATTR_TOOL), toolName));
  }

  public void countModelCall(TelemetrySession s, String provider, String model) {
    modelCallsTotal.add(1, Attributes.of(
        AttributeKey.stringKey(ATTR_SESSION_ID), s.id(),
        AttributeKey.stringKey(ATTR_PROVIDER), provider,
        AttributeKey.stringKey(ATTR_MODEL), model));
  }
}
