package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint. The constraint is active by default. Checks that two Generic that have the same supers doesn't have the same value. This problem can occur when creating
 * identical in different caches.
 * 
 * @author Nicolas Feybesse
 * @author Michaël Ory
 */
public class UnduplicateBindingConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6695533655404884865L;

	public UnduplicateBindingConstraintViolationException() {
		super();
	}

	public UnduplicateBindingConstraintViolationException(String msg) {
		super(msg);
	}
}
