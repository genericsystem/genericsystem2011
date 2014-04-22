package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.stream.Stream;

public interface AncestorsService {

	Vertex getMeta();

	Stream<Vertex> getSupersStream();

	Vertex[] getComponents();

	abstract Serializable getValue();

	default int getLevel() {
		return getMeta().getLevel() + 1;
	}

	default boolean isEngine() {
		return false;
	}

	default Engine getEngine() {
		return getMeta().getEngine();
	}

	default boolean isMeta() {
		return getLevel() == 0;
	}

	default boolean isStructural() {
		return getLevel() == 1;
	}

	default boolean isFactual() {
		return getLevel() == 2;
	}

	default boolean inheritsFrom(Vertex superVertex) {
		if (this == superVertex || equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupersStream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	default boolean isInstanceOf(Vertex metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}
}
