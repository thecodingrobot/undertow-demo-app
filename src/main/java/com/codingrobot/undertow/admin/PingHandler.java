package com.codingrobot.undertow.admin;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class PingHandler implements HttpHandler {
	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders()
						.put(Headers.CONTENT_TYPE, "text/plain")
						.put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");
		exchange.getResponseSender().send("pong");
	}
}
