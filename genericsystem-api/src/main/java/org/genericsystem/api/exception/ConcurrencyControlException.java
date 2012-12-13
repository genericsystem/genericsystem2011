package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class ConcurrencyControlException extends Exception {

	public ConcurrencyControlException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 7631483467570784262L;

}
