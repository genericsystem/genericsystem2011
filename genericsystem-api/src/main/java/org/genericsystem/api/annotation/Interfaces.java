package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
/**
 * Used to declare the Interfaces of generic.
 * A generic inherits directly or indirectly of the interfaces.
 * 
 * @author Michael Ory
 */
public @interface Interfaces {
	
	Class<?>[] value();
}
