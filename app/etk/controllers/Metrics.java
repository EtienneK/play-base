package etk.controllers;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import play.mvc.Controller;
import play.mvc.Result;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

@ApplicationScoped
public class Metrics extends Controller {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	static {
		OBJECT_MAPPER.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false));
		OBJECT_MAPPER.registerModule(new HealthCheckModule());
	}

	private MetricRegistry metricRegistry;
	private HealthCheckRegistry healthCheckRegistry;

	protected Metrics() {
	}

	@Inject
	public Metrics(MetricRegistry metricRegistry, HealthCheckRegistry healthCheckRegistry) {
		this.metricRegistry = metricRegistry;
		this.healthCheckRegistry = healthCheckRegistry;
	}

	public Result metrics() {
		try {
			response().setContentType("application/json");
			return ok(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(metricRegistry));
		} catch (JsonProcessingException e) {
			throw Throwables.propagate(e);
		}
	}

	public Result health() {
		try {
			return ok(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
					healthCheckRegistry.runHealthChecks()));
		} catch (JsonProcessingException e) {
			throw Throwables.propagate(e);
		}
	}
}
