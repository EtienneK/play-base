import static com.codahale.metrics.MetricRegistry.name;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.SimpleResult;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

public class Global extends GlobalSettings {

	private static final CdiContainer CDI_CONTAINER = CdiContainerLoader.getCdiContainer();
	static {
		CDI_CONTAINER.boot();
		ContextControl contextControl = CDI_CONTAINER.getContextControl();
		contextControl.startContext(ApplicationScoped.class);
	}

	private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
	static {
		METRIC_REGISTRY.register(name(BufferPoolMetricSet.class),
				new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
		METRIC_REGISTRY.register(name(FileDescriptorRatioGauge.class), new FileDescriptorRatioGauge());
		METRIC_REGISTRY.register(name(GarbageCollectorMetricSet.class), new GarbageCollectorMetricSet());
		METRIC_REGISTRY.register(name(MemoryUsageGaugeSet.class), new MemoryUsageGaugeSet());
		METRIC_REGISTRY.register(name(ThreadStatesGaugeSet.class), new ThreadStatesGaugeSet());
	}

	private static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();
	static {
		HEALTH_CHECK_REGISTRY.register(name(ThreadDeadlockHealthCheck.class), new ThreadDeadlockHealthCheck());
	}

	@Override
	public void onStop(Application application) {
		CDI_CONTAINER.shutdown();
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return BeanProvider.getContextualReference(CDI_CONTAINER.getBeanManager(), clazz, true);
	}

	@Override
	public Action<?> onRequest(Request request, Method actionMethod) {
		Timer responseTimer = METRIC_REGISTRY.timer(name(actionMethod.getDeclaringClass(), actionMethod.getName()));
		return new OnRequestAction(responseTimer.time());
	}

	@Produces
	@ApplicationScoped
	public static MetricRegistry getMetricRegistry() {
		return METRIC_REGISTRY;
	}

	@Produces
	@ApplicationScoped
	public static HealthCheckRegistry getHealthCheckRegistry() {
		return HEALTH_CHECK_REGISTRY;
	}

	protected static class OnRequestAction extends Action.Simple {
		private Timer.Context timerContext;

		public OnRequestAction(Timer.Context timerContext) {
			this.timerContext = timerContext;
		}

		@Override
		public Promise<SimpleResult> call(Context ctx) throws Throwable {
			return delegate.call(ctx).map(new Function<SimpleResult, SimpleResult>() {
				public SimpleResult apply(SimpleResult result) throws Throwable {
					try {
						return result;
					} finally {
						timerContext.stop();
					}
				}
			});
		}
	}

}