package org.genericsystem.impl.vertex;

public class ExistException extends RuntimeException {
	private static final long serialVersionUID = -4631985293285253439L;

	public ExistException(Vertex vertex) {
		super("Vertex already exists : " + vertex.info());
	}
}
