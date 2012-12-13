package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SystemGeneric {
	int value() default STRUCTURAL;

	static final int META = 0;
	static final int STRUCTURAL = 1;
	static final int CONCRETE = 2;
	static final int SENSOR = 3;

}
