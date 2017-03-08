package com.codingrobot.undertow.admin;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Based on {@link com.codahale.metrics.servlets.HealthCheckServlet}
 */
public class HealthChecksHandler implements HttpHandler {

	private final HealthCheckRegistry registry;
	private final ExecutorService executorService;
	private final ObjectMapper objectMapper;

	@Inject
	public HealthChecksHandler(HealthCheckRegistry registry, ExecutorService executorService, ObjectMapper objectMapper) {
		this.registry = registry;
		this.executorService = executorService;
		this.objectMapper = objectMapper;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		Map<String, HealthCheck.Result> healthChecks = registry.runHealthChecks(executorService);

		exchange.getResponseHeaders()
						.put(Headers.CONTENT_TYPE, "application/json")
						.put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");

		if (healthChecks.isEmpty()) {
			exchange.setStatusCode(StatusCodes.NOT_IMPLEMENTED);
		} else {
			if (isAllHealthy(healthChecks)) {
				exchange.setStatusCode(StatusCodes.OK);
			} else {
				exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
			}
		}

		boolean prettyPrint = exchange.getQueryParameters().containsKey("pretty");
		ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();

		byte[] bytes = writer.writeValueAsBytes(healthChecks);
		exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
	}

	private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
		for (HealthCheck.Result result : results.values()) {
			if (!result.isHealthy()) {
				return false;
			}
		}
		return true;
	}
}
