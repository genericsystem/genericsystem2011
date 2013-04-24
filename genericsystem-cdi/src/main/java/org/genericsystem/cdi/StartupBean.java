package org.genericsystem.cdi;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
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
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.jboss.weld.Container;
import org.jboss.weld.bean.ProducerMethod;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class StartupBean implements Extension {

	private Logger log = LoggerFactory.getLogger(StartupBean.class);

	@SuppressWarnings("unchecked")
	public void onStartup(@Observes AfterDeploymentValidation event, BeanManager beanManager) {

		BoundSessionContext ctx = Container.instance().deploymentManager().instance().select(BoundSessionContext.class).get();
		Map<String, Object> map = new HashMap<>();
		ctx.associate(map);
		ctx.activate();

		// log.info("------------------start initialization-----------------------");
		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		Cache cache = engine.newCache();

		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Generic.class, new AnnotationLiteral<Any>() {});
		for (Bean<?> bean : beans) {
			Type clazz = (bean instanceof ProducerMethod) ? ((ProducerMethod<?, ?>) bean).getWeldAnnotated().getBaseType() : bean.getBeanClass();
			if (clazz instanceof Class) {
				Class<? extends Generic> classToProvide = (Class<? extends Generic>) clazz;
				if (classToProvide.getAnnotation(SystemGeneric.class) != null) {
					log.info("Generic System: providing " + classToProvide);
					cache.find((Class<? extends Generic>) classToProvide);
					log.info("Generic System: " + classToProvide + " provided");
				}
			}
		}
		log.info("-------------------end initialization------------------------");
		ctx.deactivate();
		ctx.dissociate(map);

	}
}
