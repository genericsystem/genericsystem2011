package org.genericsystem.api.exception;

public class VirtualConstraintException extends ConstraintViolationException {

	private static final long serialVersionUID = -372566533541440420L;

	public VirtualConstraintException(String msg) {
		super(msg);
	}
}
