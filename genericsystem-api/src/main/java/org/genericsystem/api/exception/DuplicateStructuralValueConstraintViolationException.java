package org.genericsystem.api.exception;

public class DuplicateStructuralValueConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 4617646668336129382L;

	public DuplicateStructuralValueConstraintViolationException() {
		super();
	}

	public DuplicateStructuralValueConstraintViolationException(String msg) {
		super(msg);
	}
}
