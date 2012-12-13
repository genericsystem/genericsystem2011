package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class UniqueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6347098699041855226L;

	public UniqueConstraintViolationException(String message) {
		super(message);
	}
}
