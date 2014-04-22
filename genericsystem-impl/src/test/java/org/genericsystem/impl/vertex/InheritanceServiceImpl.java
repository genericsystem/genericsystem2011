package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class InheritanceServiceImpl extends DependenciesServiceImpl implements InheritanceService {

	private final Vertex[] supers;

	@Override
	public Stream<Vertex> getSupersStream() {
		return Arrays.stream(supers);
	}

	public InheritanceServiceImpl(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		super(meta, value, components);
		this.supers = getSupers(overrides);
	}
}
