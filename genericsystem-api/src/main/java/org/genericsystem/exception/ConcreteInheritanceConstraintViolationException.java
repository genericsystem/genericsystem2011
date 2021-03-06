package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. The constraint is active by default. Checks that no instance inherits a other instance.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class ConcreteInheritanceConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 8217219756975555723L;

	public ConcreteInheritanceConstraintViolationException(String msg) {
		super(msg);
	}
}