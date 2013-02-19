package org.genericsystem.api.exception;

public class DuplicateNameRelationConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 4617646668336129382L;

	public DuplicateNameRelationConstraintViolationException() {
		super();
	}

	public DuplicateNameRelationConstraintViolationException(String msg) {
		super(msg);
	}
}
