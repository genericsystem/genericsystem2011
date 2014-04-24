package org.genericsystem.impl.vertex.services;

import java.io.Serializable;
import org.genericsystem.impl.vertex.Statics;
import org.genericsystem.impl.vertex.Vertex;

public interface BindingService extends AncestorsService, FactoryService {

	default Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex addInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(true);
	}

	default Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex setInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(false);
	}

	default Vertex getInstance(/* Vertex[] supers, */Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, Statics.EMPTY_VERTICES, value, components).getPlugged();
	}
}
