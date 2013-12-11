package org.genericsystem.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GenericProvider {

	protected static Logger log = LoggerFactory.getLogger(GenericProvider.class);

	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.FIELD })
	public @interface InjectClass {
		@Nonbinding
		Class<?> value() default GenericImpl.class;
	}

	@Inject
	private Cache cache;

	@Produces
	@InjectClass
	public Generic getGeneric(InjectionPoint injectionPoint) {
		return cache.find(injectionPoint.getAnnotated().getAnnotation(InjectClass.class).value());
	}
}
