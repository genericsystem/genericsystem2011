package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. The constraint is active by default. Checks that the structural (attribute, relation...) name should be unique for a type.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class UniqueStructuralValueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 4617646668336129382L;

	public UniqueStructuralValueConstraintViolationException() {
		super();
	}

	public UniqueStructuralValueConstraintViolationException(String msg) {
		super(msg);
	}
}
