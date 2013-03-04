package org.genericsystem.api.exception;

/**
 * The exception is thrown if the user attempts to describe with a timestamp lower than the timestamp of the last read.
 * 
 * @author Nicolas Feybesse
 */
public class ConcurrencyControlException extends Exception {

	public ConcurrencyControlException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 7631483467570784262L;

}
