package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. Checks that the value inherits from the type imposed by the constraint.
 * 
 * @author Nicolas Feybessek
 */
public class InstanceClassConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3180066263166409718L;

	public InstanceClassConstraintViolationException(String msg) {
		super(msg);
	}

}
