package com.codingrobot.undertow.api;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import javax.inject.Inject;

public class HelloHandler implements HttpHandler {

	private final Counter visitCounter;

	@Inject
	public HelloHandler(MetricRegistry metricRegistry) {
		this.visitCounter = metricRegistry.counter("hello-visits");
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		visitCounter.inc();
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		exchange.getResponseSender().send("Hello World");
	}
}
