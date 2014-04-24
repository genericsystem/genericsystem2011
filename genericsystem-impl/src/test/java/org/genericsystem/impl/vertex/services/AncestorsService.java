package org.genericsystem.impl.vertex.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.impl.vertex.Engine;
import org.genericsystem.impl.vertex.Vertex;

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

	default boolean isAttributeOf(Vertex vertex) {
		return isEngine() || Arrays.asList(getComponents()).stream().anyMatch(component -> vertex.inheritsFrom(component) || vertex.isInstanceOf(component));
	}

	default boolean equals(Vertex meta, Serializable value, Vertex... components) {
		return this.getMeta().equals(meta) && Objects.equals(this.getValue(), value) && Arrays.equals(this.getComponents(), components);
	}
}
