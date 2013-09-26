package org.genericsystem.exception;

public class NotRemovableException extends RuntimeException {

	private static final long serialVersionUID = 6269327481627131957L;

	public NotRemovableException() {
		super();
	}

	public NotRemovableException(String msg) {
		super(msg);
	}

}
