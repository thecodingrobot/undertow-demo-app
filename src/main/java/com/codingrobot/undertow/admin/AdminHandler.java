package com.codingrobot.undertow.admin;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import io.undertow.server.handlers.PathHandler;

import javax.inject.Inject;
import java.util.concurrent.ForkJoinPool;

public class AdminHandler extends PathHandler {
	@Inject
	public AdminHandler(HealthCheckRegistry healthCheckRegistry, ObjectMapper objectMapper, Injector injector) {
		this.addExactPath("ping", new PingHandler());
		this.addExactPath("gc", new GcHandler());
		this.addExactPath("threads", new ThreadDumpHandler());
		this.addExactPath("health", new HealthChecksHandler(healthCheckRegistry, ForkJoinPool.commonPool(), objectMapper));
		this.addExactPath("metrics", injector.getInstance(MetricsHandler.class));
	}
}
