package cdi;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

public class CdiExtension implements Extension {

	private final Set<Bean<?>> startupBeans = new HashSet<Bean<?>>();

	public void detectStartupBeans(@Observes ProcessBean<?> event) {
		if (event.getAnnotated().getAnnotation(Startup.class) != null) {
			final Bean<?> bean = event.getBean();
			startupBeans.add(bean);
		}
	}

	public void startStartupBeans(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
		for (Bean<?> bean : startupBeans) {
			CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
			beanManager.getReference(bean, bean.getBeanClass(), creationalContext).toString();
		}
	}

}
