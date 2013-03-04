package org.genericsystem.api.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * The constraint is active by default.<br/>
 * Checks that no Generic isn't alive in the context.
 * 
 * @author Nicolas Feybesse
 */
public class AliveConstraintViolationException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = 1838361171620854149L;

	public AliveConstraintViolationException(String msg) {
		super(msg);
	}
}
