package org.genericsystem.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint represent the links size for an attribute.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SizeConstraint {

	/**
	 * Returns the positions of the components.
	 * 
	 * @return An array of component position.
	 */
	int[] value() default { 0 };
}
