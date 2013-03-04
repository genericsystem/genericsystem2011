package org.genericsystem.api.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that the Generic a single value for the same components.
 * 
 * @author Nicolas Feybesse
 */
public class PropertyConstraintViolationException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public PropertyConstraintViolationException(String msg) {
		super(msg);
	}

}
