package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 * 
 */
public class ConcreteInheritanceConstraintViolationException extends ConstraintViolationException {
	
	private static final long serialVersionUID = 8217219756975555723L;
	
	public ConcreteInheritanceConstraintViolationException(String msg) {
		super(msg);
	}
}