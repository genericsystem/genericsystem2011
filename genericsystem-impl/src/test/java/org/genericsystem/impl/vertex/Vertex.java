package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends InheritanceServiceImpl implements AncestorsService, DependenciesService, InheritanceService, BindingService, ComponentsInheritanceService {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	public static final Vertex[] EMPTY_VERTICES = new Vertex[] {};

	private Vertex(Vertex meta, Serializable value, Vertex[] components) {
		this(meta, EMPTY_VERTICES, value, components);
	}

	protected Vertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		super(meta, overrides, value, components);
		assert Arrays.asList(overrides).stream().allMatch(override -> getSupersStream().anyMatch(superVertex -> superVertex.inheritsFrom(override))) : "Inconsistant overrides : " + Arrays.toString(overrides)
				+ getSupersStream().collect(Collectors.toList());
		assert InheritanceService.componentsDepends(getMeta().getSingulars(), components, getMeta().getComponents()) : "Inconsistant components : " + Arrays.toString(components);
		assert getSupersStream().allMatch(superVertex -> meta.inheritsFrom(superVertex.getMeta())) : "Inconsistant supers : " + getSupersStream().collect(Collectors.toList());
		assert getSupersStream().filter(superVertex -> superVertex.inheritsFrom(meta)).count() <= 1 : "Inconsistant supers : " + getSupersStream().collect(Collectors.toList());
		assert getSupersStream().noneMatch(superVertex -> superVertex.equals(this)) : "Inconsistant supers : " + getSupersStream().collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getValue());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Vertex))
			return false;
		Vertex vertex = (Vertex) o;
		return equals(vertex.getMeta(), vertex.getValue(), vertex.getComponents());
	}

	public boolean equals(Vertex meta, Serializable value, Vertex... components) {
		return this.getMeta().equals(meta) && Objects.equals(this.getValue(), value) && Arrays.equals(this.getComponents(), components);
	}

	// public boolean inheritsFrom(Vertex superMeta, Serializable superValue, Vertex... superComponents) {
	// return SupersComputingService.inheritsFrom(meta, value, components, superMeta, superValue, superComponents);
	// }

	private boolean property = false;
	private boolean[] singulars = new boolean[/* components.length */10];

	boolean isProperty() {
		return property;
	}

	boolean[] getSingulars() {
		return singulars;
	}

	boolean isAttributeOf(Vertex vertex) {
		return isEngine() || Arrays.asList(getComponents()).stream().anyMatch(component -> vertex.inheritsFrom(component) || vertex.isInstanceOf(component));
	}

	protected Iterator<Vertex> compositesMetaIndex(Vertex meta) {
		return getComposites().stream().filter(composite -> composite.getMeta().equals(meta)).iterator();
	}

	protected Iterator<Vertex> compositesSuperIndex(Vertex superVertex) {
		return getComposites().stream().filter(composite -> composite.getSupersStream().anyMatch(next -> next.equals(superVertex))).iterator();
	}

	public Vertex getInstance(/* Vertex[] supers, */Serializable value, Vertex... components) {
		return new Vertex(this, value, components).getPlugged(false);
	}

	public String info() {
		return " (" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + Arrays.toString(getComponents()) + " ";
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

}
