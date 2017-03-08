package com.codingrobot.undertow.admin;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class GcHandler implements HttpHandler {
	@Override
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_GC", justification = "Used by the admin API")
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		Runtime.getRuntime().gc();
		exchange.getResponseHeaders()
						.put(Headers.CONTENT_TYPE, "text/plain")
						.put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");
		exchange.getResponseSender().send("Done");
	}
}
