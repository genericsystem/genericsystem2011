package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class PhantomConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3509096441924116316L;

	public PhantomConstraintViolationException(String msg) {
		super(msg);
	}
}
