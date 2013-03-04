package org.genericsystem.api.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that the Generic added does not contain the null value.
 * 
 * @author Nicolas Feybesse
 */
public class NotNullConstraintViolationException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = -3180066263166409718L;

	public NotNullConstraintViolationException(String msg) {
		super(msg);
	}

}
