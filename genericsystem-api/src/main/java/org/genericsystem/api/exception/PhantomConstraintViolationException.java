package org.genericsystem.api.exception;

//TODO ???
/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * The constraint is active by default.<br/>
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class PhantomConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 3509096441924116316L;

	public PhantomConstraintViolationException(String msg) {
		super(msg);
	}

}
