package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
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
