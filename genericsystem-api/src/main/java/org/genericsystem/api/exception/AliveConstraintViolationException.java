package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class AliveConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 1838361171620854149L;

	public AliveConstraintViolationException(String msg) {
		super(msg);
	}
}
