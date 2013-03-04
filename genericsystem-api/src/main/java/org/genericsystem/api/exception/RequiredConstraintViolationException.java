package org.genericsystem.api.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that the generic possess the information imposed by the constraint.
 * 
 * @author Nicolas Feybesse
 */
public class RequiredConstraintViolationException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public RequiredConstraintViolationException(String msg) {
		super(msg);
	}

}
