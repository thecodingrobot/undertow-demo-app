package com.codingrobot.undertow.admin;

import com.codahale.metrics.jvm.ThreadDump;
import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;

/**
 * Implementation based on {@link com.codahale.metrics.servlets.ThreadDumpServlet}
 */
public class ThreadDumpHandler implements HttpHandler {
	private static final String CONTENT_TYPE = "text/plain";
	private transient ThreadDump threadDump;

	ThreadDumpHandler() {
		try {
			// Some PaaS like Google App Engine blacklist java.lang.managament
			this.threadDump = new ThreadDump(ManagementFactory.getThreadMXBean());
		} catch (NoClassDefFoundError ncdfe) {
			this.threadDump = null; // we won't be able to provide thread dump
		}
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.setStatusCode(StatusCodes.OK);
		exchange.getResponseHeaders()
						.put(Headers.CONTENT_TYPE, CONTENT_TYPE)
						.put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");


		if (threadDump == null) {
			exchange.getResponseSender().send("Sorry your runtime environment does not allow to dump threads.");
			return;
		}

		final Sender output = exchange.getResponseSender();
		ByteBuffer buf = ByteBuffer.allocateDirect(32 * 1024);
		try {
			ByteBufferBackedOutputStream outputStream = new ByteBufferBackedOutputStream(buf);
			threadDump.dump(outputStream);
			buf.rewind();
			output.send(buf);
		} finally {
			output.close();
		}
	}
}
