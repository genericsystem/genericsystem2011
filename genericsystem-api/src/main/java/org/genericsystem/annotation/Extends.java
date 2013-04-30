package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to declare the Extends of generic. A generic inherits directly of the supers.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Extends {

	/**
	 * Returns the class of the implicit super.
	 * 
	 * @return The class of the implicit super.
	 */
	Class<?> value();

	/**
	 * Returns the class of the supers.
	 * 
	 * @return An array of class of the supers.
	 */
	Class<?>[] others() default {};
}
