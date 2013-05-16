package org.genericsystem.exception;

public class FunctionalConsistencyViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -6461431364555871428L;

	public FunctionalConsistencyViolationException(String msg) {
		super(msg);
	}
}
