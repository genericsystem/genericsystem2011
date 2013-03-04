package org.genericsystem.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The System Property to constrain the type of a value.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface InstanceClassConstraint {

	/**
	 * Returns the class of type constrained.
	 * 
	 * @return The class.
	 */
	Class<?> value();

}
