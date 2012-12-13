package org.genericsystem.api.exception;

public class SuperRuleConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 1838361171620854149L;

	public SuperRuleConstraintViolationException() {
		super();
	}

	public SuperRuleConstraintViolationException(String msg) {
		super(msg);
	}
}
