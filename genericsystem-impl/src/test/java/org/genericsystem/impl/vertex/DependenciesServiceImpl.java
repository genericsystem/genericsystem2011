package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class DependenciesServiceImpl extends AncestorsServiceImpl implements DependenciesService {

	protected final Map<Vertex, Vertex> instances = new LinkedHashMap<>();
	protected final Map<Vertex, Vertex> inheritings = new LinkedHashMap<>();
	protected final Map<Vertex, Vertex> composites = new LinkedHashMap<>();

	public DependenciesServiceImpl(Vertex meta, Serializable value, Vertex[] components) {
		super(meta, value, components);
	}

	@Override
	public Set<Vertex> getInstances() {
		return instances.keySet();
	}

	@Override
	public Set<Vertex> getInheritings() {
		return inheritings.keySet();
	}

	@Override
	public Set<Vertex> getComposites() {
		return composites.keySet();
	}

	@Override
	public boolean isPlugged() throws NotFoundException {
		return this == getPlugged(false);
	}

	@Override
	public Vertex getPlugged(boolean throwNotFoundException) throws NotFoundException {
		Vertex result = getMeta().instances.get(this);
		if (result == null)
			if (throwNotFoundException)
				throw new NotFoundException((Vertex) this);
		return result;
	}

	@Override
	public Vertex plug(boolean throwExistException) {
		getSupersStream().forEach(superGeneric -> putThisIfAbsentOfIndex(superGeneric.inheritings, throwExistException));
		Arrays.asList(getComponents()).forEach(component -> putThisIfAbsentOfIndex(component.composites, throwExistException));
		return putThisIfAbsentOfIndex(getMeta().instances, throwExistException);
	}

	private Vertex putThisIfAbsentOfIndex(Map<Vertex, Vertex> index, boolean throwExistException) throws ExistException {
		if (!index.containsKey(this)) {
			Vertex result = index.put((Vertex) this, (Vertex) this);
			assert result == null; // TODO if result != null ?
			return (Vertex) this;
		}

		if (throwExistException)
			throw new ExistException((Vertex) this);
		return index.get(this);
	}
}
