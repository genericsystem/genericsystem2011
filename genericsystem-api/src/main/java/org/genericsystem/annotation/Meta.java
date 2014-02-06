package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.core.Engine;

/**
 * Used to declare the Extends of generic. A generic inherits directly of the supers.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Meta {

	/**
	 * The meta class
	 * 
	 * @return meta class
	 */
	Class<?> value() default Engine.class;
}
