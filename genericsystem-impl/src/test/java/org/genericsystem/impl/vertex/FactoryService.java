package org.genericsystem.impl.vertex;

import java.io.Serializable;
import org.genericsystem.impl.vertex.DependenciesService.Dependencies;

public interface FactoryService extends AncestorsService {

	public static interface Factory {
		default Dependencies buildDependency(Vertex vertex) {
			return new DependenciesImpl();
		}

		default Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			return new Vertex(meta, overrides, value, components);
		}
	}

	default Factory getFactory() {
		return getEngine().getFactory();
	}
}
