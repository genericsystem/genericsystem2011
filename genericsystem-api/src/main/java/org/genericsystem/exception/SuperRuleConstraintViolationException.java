package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * The constraint is active by default.<br/>
 * Checks that each super is a parent of the Generic.
 * 
 * @author Nicolas Feybesse
 */
public class SuperRuleConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 1838361171620854149L;

	public SuperRuleConstraintViolationException() {
		super();
	}

	public SuperRuleConstraintViolationException(String msg) {
		super(msg);
	}
}
