package org.genericsystem.impl.vertex.services;

import java.util.Arrays;
import java.util.Iterator;
import org.genericsystem.impl.vertex.ExistException;
import org.genericsystem.impl.vertex.NotFoundException;
import org.genericsystem.impl.vertex.Snapshot;
import org.genericsystem.impl.vertex.Vertex;

public interface DependenciesService extends AncestorsService, FactoryService {

	public abstract static class Dependencies extends Snapshot<Vertex> {

		public Vertex remove(Vertex vertex, boolean throwNotFoundException) {
			boolean result = remove(vertex);
			if (!result && throwNotFoundException)
				throw new NotFoundException(vertex);
			return vertex;
		};

		public abstract boolean remove(Vertex vertex);

		public abstract Vertex bind(Vertex vertex, boolean throwExistException) throws ExistException;

	}

	Dependencies getInstances();

	Dependencies getInheritings();

	Dependencies getComposites();

	default Iterator<Vertex> compositesMetaIndex(Vertex meta) {
		return getComposites().stream().filter(composite -> composite.getMeta().equals(meta)).iterator();
	}

	default Iterator<Vertex> compositesSuperIndex(Vertex superVertex) {
		return getComposites().stream().filter(composite -> composite.getSupersStream().anyMatch(next -> next.equals(superVertex))).iterator();
	}

	default boolean isPlugged() throws NotFoundException {
		return this == getPlugged(false);
	}

	default Vertex getPlugged(boolean throwNotFoundException) throws NotFoundException {
		Vertex result = getMeta().getInstances().get((Vertex) this);
		if (result == null)
			if (throwNotFoundException)
				throw new NotFoundException((Vertex) this);
		return result;
	}

	default Vertex plug(boolean throwExistException) {
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().bind((Vertex) this, throwExistException));
		Arrays.asList(getComponents()).forEach(component -> component.getComposites().bind((Vertex) this, throwExistException));
		return getMeta().getInstances().bind((Vertex) this, throwExistException);
	}

	default Vertex unplug(boolean throwNotFoundException) throws NotFoundException {
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().remove((Vertex) this, throwNotFoundException));
		Arrays.asList(getComponents()).forEach(component -> component.getComposites().remove((Vertex) this, throwNotFoundException));
		return getMeta().getInstances().remove((Vertex) this, throwNotFoundException);
	}

}
