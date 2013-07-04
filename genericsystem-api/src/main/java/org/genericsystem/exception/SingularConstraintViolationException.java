package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks that the Generic a only one single value.
 * 
 * @author Nicolas Feybesse
 */
public class SingularConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3620033908726821166L;

	public SingularConstraintViolationException(String string) {
		super(string);
	}

}
