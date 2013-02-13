package org.genericsystem.api.exception;

public class UnduplicateBindingConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 6695533655404884865L;

	public UnduplicateBindingConstraintViolationException() {
		super();
	}

	public UnduplicateBindingConstraintViolationException(String msg) {
		super(msg);
	}
}
