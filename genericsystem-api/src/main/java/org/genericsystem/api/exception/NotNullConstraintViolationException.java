package org.genericsystem.api.exception;

public class NotNullConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3180066263166409718L;

	public NotNullConstraintViolationException(String msg) {
		super(msg);
	}

}
