package org.genericsystem.api.exception;

/**
 * It's the general runtime exception.
 * 
 * @author Nicolas Feybesse
 */
public class RollbackException extends RuntimeException {

	private static final long serialVersionUID = -3498138328936732076L;

	public RollbackException(String message) {
		super(message);
	}

	public RollbackException(Throwable cause) {
		super(cause);
	}
}
