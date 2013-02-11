package org.genericsystem.api.exception;

public class PhantomConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 3509096441924116316L;

	public PhantomConstraintViolationException(String msg) {
		super(msg);
	}

}
