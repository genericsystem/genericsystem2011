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

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class StartupBean implements Extension {

	private Logger log = LoggerFactory.getLogger(StartupBean.class);

	// public void onStartup(@Observes ContainerInitialized init) {
	@SuppressWarnings("unchecked")
	public void onStartup(@Observes AfterDeploymentValidation event, BeanManager beanManager) {

		// BoundSessionContext ctx = Container.instance().deploymentManager().instance().select(BoundSessionContext.class).get();
		// Map<String, Object> map = new HashMap<>();
		// ctx.associate(map);
		// ctx.activate();

		log.info("------------------start initialization-----------------------");
		UserClasses userClasses = BeanManagerUtils.getContextualInstance(beanManager, UserClasses.class);
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {
		});
		for (Bean<?> bean : beans) {
			Type clazz = bean.getBeanClass();
			// (bean instanceof ProducerMethod) ? ((ProducerMethod<?, ?>) bean).getWeldAnnotated().getBaseType() : bean.getBeanClass();
			if (clazz instanceof Class) {
				Class<? extends Generic> classToProvide = (Class<? extends Generic>) clazz;
				if (classToProvide.getAnnotation(SystemGeneric.class) != null) {
					log.info("Generic System: providing " + classToProvide);
					// cache.find((Class<? extends Generic>) classToProvide);
					userClasses.addUserClasse(classToProvide);
				}
			}
		}
		log.info("-------------------end initialization------------------------");
		// ctx.deactivate();
		// ctx.dissociate(map);
	}
}
