import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import static com.codahale.metrics.MetricRegistry.*;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadDeadlockDetector;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.SimpleResult;
import play.mvc.Http.Context;
import play.mvc.Http.Request;

public class Global extends GlobalSettings {

    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    private CdiContainer cdiContainer;

    @Override
    public void onStart(Application application) {
        // JVM Metrics
        METRIC_REGISTRY.register(name(BufferPoolMetricSet.class),
                new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        METRIC_REGISTRY.register(name(FileDescriptorRatioGauge.class), new FileDescriptorRatioGauge());
        METRIC_REGISTRY.register(name(GarbageCollectorMetricSet.class), new GarbageCollectorMetricSet());
        METRIC_REGISTRY.register(name(MemoryUsageGaugeSet.class), new MemoryUsageGaugeSet());
        METRIC_REGISTRY.register(name(ThreadStatesGaugeSet.class), new ThreadStatesGaugeSet());

        // CDI Container
        cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();

        ContextControl contextControl = cdiContainer.getContextControl();
        contextControl.startContext(ApplicationScoped.class);
    }

    @Override
    public void onStop(Application application) {
        cdiContainer.shutdown();
    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) {
        return BeanProvider.getContextualReference(cdiContainer.getBeanManager(), clazz, true);
    }

    @Override
    public Action<?> onRequest(Request request, Method actionMethod) {
        Timer responseTimer = METRIC_REGISTRY.timer(name(actionMethod.getDeclaringClass(), actionMethod.getName()));
        return new OnRequestAction(responseTimer.time());
    }

    @Produces
    @ApplicationScoped
    public MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    private static class OnRequestAction extends Action.Simple {
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