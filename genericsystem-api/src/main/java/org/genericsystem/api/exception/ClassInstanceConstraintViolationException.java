package org.genericsystem.api.exception;

/**
 * 
 * @author Nicolas Feybesse
 */
public class ClassInstanceConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3180066263166409718L;

	public ClassInstanceConstraintViolationException(String msg) {
		super(msg);
	}

}
