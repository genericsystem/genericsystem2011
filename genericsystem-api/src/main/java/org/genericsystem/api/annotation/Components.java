package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
/**
 * The compositions of a generic.
 * 
 * @author Michael Ory
 */
public @interface Components {
	
	Class<?>[] value();
}
