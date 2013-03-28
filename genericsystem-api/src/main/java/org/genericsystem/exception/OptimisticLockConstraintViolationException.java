package org.genericsystem.exception;

//TODO ???
/**
 * Is triggered if remove alive Generic.
 * 
 * @author Nicolas Feybesse
 */
public class OptimisticLockConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6347098699041855226L;

	public OptimisticLockConstraintViolationException(String message) {
		super(message);
	}
}
