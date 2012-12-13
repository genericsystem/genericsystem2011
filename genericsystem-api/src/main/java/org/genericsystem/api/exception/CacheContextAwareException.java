package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class CacheContextAwareException extends RuntimeException {

	private static final long serialVersionUID = 5740499255956575063L;

	public CacheContextAwareException(String message) {
		super(message);
	}
}
