package org.genericsystem.impl.vertex;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7472730943638836698L;

	public NotFoundException(Vertex vertex) {
		super("Vertex not found : " + vertex.info());
	}
}
