package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. Checks the Generic is abstract.
 * 
 * @author Nicolas Feybesse
 */
public class VirtualConstraintException extends ConstraintViolationException {

	private static final long serialVersionUID = -372566533541440420L;

	public VirtualConstraintException(String msg) {
		super(msg);
	}
}
