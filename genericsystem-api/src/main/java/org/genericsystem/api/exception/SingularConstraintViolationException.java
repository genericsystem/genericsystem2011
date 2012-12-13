package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class SingularConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3620033908726821166L;

	public SingularConstraintViolationException(String string) {
		super(string);
	}

}
