package org.genericsystem.impl.vertex;

import java.io.Serializable;

public interface BindingService extends AncestorsService {

	default Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(Vertex.EMPTY_VERTICES, value, components);
	}

	default Vertex addInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return bind(true, (Vertex) BindingService.this, overrides, value, components);
	}

	default Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(Vertex.EMPTY_VERTICES, value, components);
	}

	default Vertex setInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return bind(false, (Vertex) BindingService.this, overrides, value, components);
	}

	default Vertex bind(boolean throwExistsException, Vertex meta, Vertex[] overrides, Serializable value, Vertex... components) {
		Vertex result = new Vertex(meta, overrides, value, components).plug(throwExistsException);
		// assert Arrays.asList(overrides).stream().allMatch(overVertex -> result.getSupersStream().anyMatch(superVertex -> superVertex.inheritsFrom(overVertex))) : "Result : " + result.info() + " don't satisfy overrides : " + Arrays.toString(overrides);
		return result;
	}
}
