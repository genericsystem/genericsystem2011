package org.genericsystem.exception;

/**
 * Is triggered if the constraint is positioned and that the generic does not respect the constraint.<br/>
 * The constraint is active by default.<br/>
 * Checks that the Generic added to the same Engine as the Context.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class EngineConsistencyConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 1525561745725526259L;

	public EngineConsistencyConstraintViolationException() {
		super();
	}

	public EngineConsistencyConstraintViolationException(String msg) {
		super(msg);
	}
}
