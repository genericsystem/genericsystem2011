package org.genericsystem.api.exception;

/**
 * Is triggered if you remove a Generic that a alive dependency.
 * 
 * @author Nicolas Feybesse
 */
public class ReferentialIntegrityConstraintViolationException extends AbstractConstraintViolationException {

	private static final long serialVersionUID = 1783386811956942568L;

	public ReferentialIntegrityConstraintViolationException(String msg) {
		super(msg);
	}

}
