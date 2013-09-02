package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a class as managed by Generic System.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SystemGeneric {

	/**
	 * Returns the meta level.
	 * 
	 * @return The meta level.
	 */
	int value() default STRUCTURAL;

	/**
	 * for engine, meta-attribute.
	 */
	static final int META = 0;

	/**
	 * for types, attributes, relations...
	 */
	static final int STRUCTURAL = 1;

	/**
	 * for instances, holders, links...
	 */
	static final int CONCRETE = 2;

	/**
	 * No use.
	 */
	static final int SENSOR = 3;

}
