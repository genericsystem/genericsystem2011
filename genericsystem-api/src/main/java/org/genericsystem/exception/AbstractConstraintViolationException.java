package org.genericsystem.exception;

/**
 * It's the general exception.
 * 
 * @author Nicolas Feybesse
 */
public abstract class AbstractConstraintViolationException extends Exception {

	private static final long serialVersionUID = 4647517844227534027L;

	public AbstractConstraintViolationException() {
	};

	public AbstractConstraintViolationException(String msg) {
		super(msg);
	}
}
