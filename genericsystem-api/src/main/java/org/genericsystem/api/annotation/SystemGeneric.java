package org.genericsystem.api.annotation;

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
	 * The default behavior defined the nature of the position values of the SystemProperty.<br/>
	 * he defined if the positioning activates or deactivates the SystemProperty.<br/>
	 * By default it's false. The positioning activates the SystemProperty.
	 * 
	 * @return The default behavior.
	 */
	boolean defaultBehavior() default false;

	/**
	 * Meta it's for meta system. Actualy it's for Engine.
	 */
	static final int META = 0;

	/**
	 * Structural it's for the structure (types, attributes, relationships ...).
	 */
	static final int STRUCTURAL = 1;

	/**
	 * Concrete it's instanciation level.
	 */
	static final int CONCRETE = 2;

	/**
	 * No use.
	 */
	static final int SENSOR = 3;

}
