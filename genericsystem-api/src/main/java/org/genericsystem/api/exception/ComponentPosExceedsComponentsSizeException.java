package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class ComponentPosExceedsComponentsSizeException extends RuntimeException {

	private static final long serialVersionUID = 5740499255956575063L;

	public ComponentPosExceedsComponentsSizeException(String message) {
		super(message);
	}
}
