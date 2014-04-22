package org.genericsystem.impl.vertex;

import java.io.Serializable;

public abstract class AncestorsServiceImpl implements AncestorsService {
	private final Serializable value;
	private final Vertex meta;
	private final Vertex[] components;

	public AncestorsServiceImpl(Vertex meta, Serializable value, Vertex[] components) {
		this.meta = isEngine() ? (Vertex) this : meta;
		this.value = getEngine().getCachedValue(value);
		this.components = components;
	}

	@Override
	public Vertex getMeta() {
		return meta;
	}

	@Override
	public Vertex[] getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}
}
