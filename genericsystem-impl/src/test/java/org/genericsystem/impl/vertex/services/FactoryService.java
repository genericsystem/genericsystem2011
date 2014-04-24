package org.genericsystem.impl.vertex.services;

import java.io.Serializable;
import org.genericsystem.impl.vertex.DependenciesImpl;
import org.genericsystem.impl.vertex.Vertex;
import org.genericsystem.impl.vertex.services.DependenciesService.Dependencies;

public interface FactoryService extends AncestorsService {

	public static interface Factory {
		default Dependencies buildDependency(Vertex vertex) {
			return new DependenciesImpl();
		}

		default Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			return new DefaultVertex(meta, overrides, value, components) {};
		}
	}

	static class DefaultVertex extends Vertex {
		protected DefaultVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			super(meta, overrides, value, components);
		}
	}

	default Factory getFactory() {
		return getEngine().getFactory();
	}
}
