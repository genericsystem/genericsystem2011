package org.genericsystem.api.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks the Generic is abstract.
 * 
 * @author Nicolas Feybesse
 */
public class VirtualConstraintException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = -372566533541440420L;

	public VirtualConstraintException(String msg) {
		super(msg);
	}
}
