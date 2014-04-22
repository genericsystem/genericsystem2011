package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class SizeConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -1821648654519517386L;

	public SizeConstraintViolationException(String string) {
		super(string);
	}

}
