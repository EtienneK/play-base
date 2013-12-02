package controllers.server;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

import play.mvc.Controller;
import play.mvc.Result;

@ApplicationScoped
public class Metrics extends Controller {

    private MetricRegistry metricRegistry;
    private ObjectMapper objectMapper;

    protected Metrics() {
    }

    @Inject
    public Metrics(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));
    }

    public Result index() {
        try {
            response().setContentType("application/json");
            return ok(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metricRegistry));
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

}
