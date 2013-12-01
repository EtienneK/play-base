import javax.enterprise.context.ApplicationScoped;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

	private CdiContainer cdiContainer;

	@Override
	public void onStart(Application application) {
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

}