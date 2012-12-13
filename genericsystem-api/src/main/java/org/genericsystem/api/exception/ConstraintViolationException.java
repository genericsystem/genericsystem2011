package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class ConstraintViolationException extends Exception {

	private static final long serialVersionUID = 4647517844227534027L;

	public ConstraintViolationException() {

	};

	public ConstraintViolationException(String msg) {
		super(msg);
	}
}
