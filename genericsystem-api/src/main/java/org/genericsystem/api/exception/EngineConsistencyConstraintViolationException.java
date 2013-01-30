package org.genericsystem.api.exception;

public class EngineConsistencyConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 1525561745725526259L;

	public EngineConsistencyConstraintViolationException() {
		super();
	}

	public EngineConsistencyConstraintViolationException(String msg) {
		super(msg);
	}
}
