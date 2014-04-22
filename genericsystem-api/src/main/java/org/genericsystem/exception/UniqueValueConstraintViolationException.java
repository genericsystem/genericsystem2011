package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br>
 * Checks the value can not be used that by a single instance.
 * 
 * @author Nicolas Feybesse
 */
public class UniqueValueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6347098699041855226L;

	public UniqueValueConstraintViolationException(String message) {
		super(message);
	}
}
