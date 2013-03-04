package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * The constraint is active by default.<br/>
 * Checks that the structural (attribute, relation...) name should be unique for a type.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class DuplicateStructuralValueConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 4617646668336129382L;

	public DuplicateStructuralValueConstraintViolationException() {
		super();
	}

	public DuplicateStructuralValueConstraintViolationException(String msg) {
		super(msg);
	}
}
