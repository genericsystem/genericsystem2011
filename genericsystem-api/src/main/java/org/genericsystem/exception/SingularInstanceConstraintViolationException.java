package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that the Generic is a singleton.
 * 
 * @author Nicolas Feybesse
 */
public class SingularInstanceConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7293718200418992241L;

	public SingularInstanceConstraintViolationException() {
		super();
	}

	public SingularInstanceConstraintViolationException(String msg) {
		super(msg);
	}

}
