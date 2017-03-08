package com.codingrobot.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.StatusCodes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnhandledExceptionHandler implements HttpHandler {
	private final Logger logger = LogManager.getLogger(UnhandledExceptionHandler.class);

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		Throwable attachment = exchange.getAttachment(ExceptionHandler.THROWABLE);
		logger.error(String.format("Unhandled exception. Reason: [%s]", attachment.getMessage()), attachment);

		exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
		exchange.endExchange();
	}
}
