package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
/**
 * Constrain the type of a value.
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public @interface InstanceClassConstraint {

	Class<?> value();

}
