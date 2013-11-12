package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * Checks the value can not be used that by a single instance.
 * 
 * Triggered when two or more generics with same values are present in engine
 * 
 * @author Alexei KLENIN - aklenin@middlewarefactory.com
 */
public class AmbiguousSelectionException extends ConstraintViolationException {

	private static final long serialVersionUID = 1L;

	public AmbiguousSelectionException(String message) {
		super(message);
	}

}
