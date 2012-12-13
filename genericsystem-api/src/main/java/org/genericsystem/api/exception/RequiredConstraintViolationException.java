package org.genericsystem.api.exception;

public class RequiredConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public RequiredConstraintViolationException(String msg) {
		super(msg);
	}

}
