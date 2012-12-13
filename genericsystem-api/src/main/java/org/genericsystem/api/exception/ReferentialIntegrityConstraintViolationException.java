package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class ReferentialIntegrityConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 1783386811956942568L;

	public ReferentialIntegrityConstraintViolationException(String msg) {
		super(msg);
	}

}
