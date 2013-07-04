package org.genericsystem.exception;

public class ExistsException extends ConstraintViolationException {

	private static final long serialVersionUID = 3025194866632437953L;

	public ExistsException(String msg) {
		super(msg);
	}
}