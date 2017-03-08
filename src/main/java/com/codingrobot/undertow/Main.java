package com.codingrobot.undertow;

import com.codingrobot.undertow.admin.AdminHandler;
import com.codingrobot.undertow.admin.AdminModule;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.undertow.Undertow;
import io.undertow.server.handlers.GracefulShutdownHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	public static void main(String[] args) {
		Logger logger = LogManager.getLogger(Main.class);

		Injector injector = Guice.createInjector(new AdminModule(), new ObjectMapperModule());


		GracefulShutdownHandler rootHandler = MainHandler.factory(injector);

		Undertow undertow = Undertow.builder()
						.addHttpListener(8080, "localhost", rootHandler)
						.addHttpListener(8081, "localhost", injector.getInstance(AdminHandler.class))
						.build();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Shutdown");
			long shutdownStart = System.currentTimeMillis();
			rootHandler.shutdown();
			rootHandler.addShutdownListener(isSuccessful -> undertow.stop());

			try {
				rootHandler.awaitShutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("Shutdown completed in [{}] ms", System.currentTimeMillis() - shutdownStart);
		}));

		undertow.start();
	}
}
