package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
/**
 * The value is a boolean value.
 * 
 * @author Michael Ory
 */
public @interface BooleanValue {
	boolean value();
}
