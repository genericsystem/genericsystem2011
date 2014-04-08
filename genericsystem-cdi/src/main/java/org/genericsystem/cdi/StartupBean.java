package org.genericsystem.cdi;

import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.cdi.event.EventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class StartupBean implements Extension {

	private Logger log = LoggerFactory.getLogger(StartupBean.class);

	public void onStartup(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
		log.info("------------------start initialization-----------------------");
		UserClassesProvider userClasses = BeanProvider.getContextualReference(UserClassesProvider.class);// TODO BeanManagerUtils.getContextualInstance(beanManager, UserClassesProvider.class);
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {
		});
		for (Bean<?> bean : beans) {
			Type clazz = bean.getBeanClass();
			if (clazz instanceof Class) {
				Class<?> classToProvide = (Class<?>) clazz;
				if (classToProvide.getAnnotation(SystemGeneric.class) != null) {
					log.info("Generic System: providing " + classToProvide);
					userClasses.addUserClasse(classToProvide);
				}
			}
		}
		EventLauncher eventLauncher = BeanProvider.getContextualReference(EventLauncher.class);// TODO BeanManagerUtils.getContextualInstance(beanManager, EventLauncher.class);
		eventLauncher.launchStartEvent();
		log.info("-------------------end initialization------------------------");
	}

}
