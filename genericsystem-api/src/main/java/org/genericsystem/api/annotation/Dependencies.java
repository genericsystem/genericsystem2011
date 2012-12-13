package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
/**
 * Used to declare the dependencies of generic.
 * 
 * @author Michael Ory
 */
public @interface Dependencies {
	Class<?>[] value();
}