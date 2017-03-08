package com.codingrobot.undertow.admin;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.google.inject.Binder;
import com.google.inject.Module;

import javax.inject.Singleton;
import java.lang.management.ManagementFactory;

public class AdminModule implements Module {

	private boolean metricsAreRegistered;

	@Override
	public void configure(Binder binder) {
		binder.bind(HealthCheckRegistry.class).in(Singleton.class);


		MetricRegistry metricRegistry = new MetricRegistry();
		binder.bind(MetricRegistry.class).toInstance(metricRegistry);
		registerMetrics(metricRegistry);

	}

	private void registerMetrics(MetricRegistry metricRegistry) {
		if (metricsAreRegistered) {
			return;
		}
		metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
		metricRegistry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
		metricRegistry.register("jvm.classloader", new ClassLoadingGaugeSet());
		metricRegistry.register("jvm.filedescriptor", new FileDescriptorRatioGauge());
		metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
		metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
		metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());

		JmxReporter.forRegistry(metricRegistry).build().start();
		metricsAreRegistered = true;
	}
}
