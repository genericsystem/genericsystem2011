package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. Checks that the generic possess the information imposed by the constraint.
 * 
 * @author Nicolas Feybesse
 */
public class RequiredConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public RequiredConstraintViolationException(String msg) {
		super(msg);
	}

}
