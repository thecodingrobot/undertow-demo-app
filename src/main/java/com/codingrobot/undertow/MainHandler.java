package com.codingrobot.undertow;

import com.codingrobot.undertow.api.HelloHandler;
import com.google.inject.Injector;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.StuckThreadDetectionHandler;

public class MainHandler {

	public static GracefulShutdownHandler factory(Injector injector) {

		HttpHandler pathHandler = Handlers.path()
						.addExactPath("/", injector.getInstance(HelloHandler.class))
						.addExactPath("/err", exchange -> {
							throw new IllegalArgumentException("Error");
						});

		HttpHandler exceptionHandler = Handlers.exceptionHandler(pathHandler)
						.addExceptionHandler(Exception.class, new UnhandledExceptionHandler());

		GracefulShutdownHandler rootHandler = Handlers.gracefulShutdown(new StuckThreadDetectionHandler.Wrapper().wrap(exceptionHandler));

		return rootHandler;
	}
}
