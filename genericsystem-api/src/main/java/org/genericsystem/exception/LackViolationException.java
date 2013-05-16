package org.genericsystem.exception;

public class LackViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3025194866632437953L;

	public LackViolationException(String msg) {
		super(msg);
	}
}