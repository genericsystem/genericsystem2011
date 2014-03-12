package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that two generic can not have the same value, same meta and same components.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class GetGenericConstraintVioliationException extends ConstraintViolationException {

	private static final long serialVersionUID = -372566533541440420L;

	public GetGenericConstraintVioliationException(String msg) {
		super(msg);
	}
}
