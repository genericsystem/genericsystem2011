package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.impl.vertex.Engine.ValueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex implements AncestorsService, DependenciesService, InheritanceService, BindingService, ComponentsInheritanceService, FactoryService, DisplayService, SystemPropertiesService {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Serializable value;
	private final Vertex meta;
	private final Vertex[] components;
	private final Dependencies instances;
	private final Dependencies inheritings;
	private final Dependencies composites;
	private final Vertex[] supers;

	private Vertex(Vertex meta, Serializable value, Vertex[] components) {
		this(meta, Statics.EMPTY_VERTICES, value, components);
	}

	// Engine constructor
	protected Vertex(Factory factory) {
		((Engine) this).valueCache = new ValueCache();
		((Engine) this).factory = factory;
		this.meta = this;
		this.value = ((Engine) this).getCachedValue(Statics.ENGINE_VALUE);
		this.components = Statics.EMPTY_VERTICES;
		this.instances = getFactory().buildDependency(this);
		this.inheritings = getFactory().buildDependency(this);
		this.composites = getFactory().buildDependency(this);
		this.supers = Statics.EMPTY_VERTICES;
	}

	protected Vertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		this.meta = isEngine() ? (Vertex) this : meta;
		this.value = getEngine().getCachedValue(value);
		this.components = components;
		this.instances = getFactory().buildDependency(this);
		this.inheritings = getFactory().buildDependency(this);
		this.composites = getFactory().buildDependency(this);
		this.supers = getSupers(overrides);
		checkOverrides(overrides);
		checkSupers();
	}

	@Override
	public Vertex getMeta() {
		return meta;
	}

	@Override
	public Vertex[] getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public Dependencies getInstances() {
		return instances;
	}

	@Override
	public Dependencies getInheritings() {
		return inheritings;
	}

	@Override
	public Dependencies getComposites() {
		return composites;
	}

	@Override
	public Stream<Vertex> getSupersStream() {
		return Arrays.stream(supers);
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

	private boolean property = false;
	private boolean[] singulars = new boolean[/* components.length */10];

	boolean isProperty() {
		return property;
	}

	boolean[] getSingulars() {
		return singulars;
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

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

}
