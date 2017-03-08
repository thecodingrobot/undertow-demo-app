package com.codingrobot.undertow.admin;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import javax.inject.Inject;
import java.util.Map;

public class MetricsHandler implements HttpHandler {

	private final MetricRegistry metricRegistry;
	private final ObjectMapper objectMapper;

	@Inject
	public MetricsHandler(MetricRegistry metricRegistry, ObjectMapper objectMapper) {
		this.metricRegistry = metricRegistry;
		this.objectMapper = objectMapper;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		Map<String, Metric> metrics = metricRegistry.getMetrics();

		exchange.getResponseHeaders()
						.put(Headers.CONTENT_TYPE, "application/json")
						.put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");

		boolean prettyPrint = exchange.getQueryParameters().containsKey("pretty");
		ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();

		String json = writer.writeValueAsString(metrics);
		exchange.getResponseSender().send(json);
	}
}
