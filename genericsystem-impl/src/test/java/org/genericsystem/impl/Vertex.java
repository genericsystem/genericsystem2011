package org.genericsystem.impl;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Vertex {

	private final Serializable value;
	private final Vertex meta;
	private final Vertex[] supers;
	private final Vertex[] components;

	private final Map<Vertex, Vertex> instances = new LinkedHashMap<>();
	private final Map<Vertex, Vertex> inheritings = new LinkedHashMap<>();
	private final Map<Vertex, Vertex> composites = new LinkedHashMap<>();

	public static final Vertex[] EMPTY_VERTICES = new Vertex[] {};

	private Vertex(Vertex meta, Serializable value, Vertex[] components) {
		this(meta, EMPTY_VERTICES, value, components);
	}

	private Vertex(Vertex meta, Vertex[] supers, Serializable value, Vertex[] components) {
		this.meta = isEngine() ? this : meta;
		this.value = isEngine() ? value : getEngine().getCachedValue(value);
		this.supers = supers;
		this.components = components;
		assert componentsInherits(components, this.meta.components) : "Inconsistant components : " + Arrays.toString(components);
		assert Arrays.asList(supers).stream().allMatch(superVertex -> meta.inheritsFrom(superVertex.getMeta())) : "Inconsistant supers : " + Arrays.toString(supers);
		assert Arrays.asList(supers).stream().allMatch(superVertex -> componentsInherits(components, superVertex.components)) : "Inconsistant supers : " + Arrays.toString(supers);
		assert Arrays.asList(supers).stream().filter(superVertex -> superVertex.inheritsFrom(meta)).count() <= 1 : "Inconsistant supers : " + Arrays.toString(supers);
	}

	public Set<Vertex> getInstances() {
		return instances.keySet();
	}

	public Set<Vertex> getInheritings() {
		return inheritings.keySet();
	}

	public Set<Vertex> getComposites() {
		return composites.keySet();
	}

	private Vertex plug(boolean throwExistException) {
		Arrays.asList(supers).forEach(superGeneric -> indexVertex(superGeneric.inheritings, throwExistException));
		Arrays.asList(components).forEach(component -> indexVertex(component.composites, throwExistException));
		return indexVertex(meta.instances, throwExistException);
	}

	private Vertex indexVertex(Map<Vertex, Vertex> index, boolean throwExistException) throws ExistException {
		Vertex result = index.put(this, this);
		if (result != null) {
			if (throwExistException)
				throw new ExistException();
			return result;
		}
		return this;
	}

	public Serializable getValue() {
		return value;
	}

	public Vertex getMeta() {
		return meta;
	}

	public Vertex[] getSupers() {
		return supers;
	}

	public Vertex[] getComponents() {
		return components;
	}

	public int getLevel() {
		return meta.getLevel() + 1;
	}

	public boolean isEngine() {
		return false;
	}

	public Engine getEngine() {
		return getMeta().getEngine();
	}

	public static class Engine extends Vertex {
		private final static String ENGINE_VALUE = "Engine";
		private final ValueCache valueCache = new ValueCache();

		public Engine() {
			super(null, new Vertex[] {}, ENGINE_VALUE, new Vertex[] {});
		}

		@Override
		public boolean isEngine() {
			return true;
		}

		@Override
		public Engine getEngine() {
			return this;
		}

		@Override
		public int getLevel() {
			return 0;
		}

		public Serializable getCachedValue(Serializable value) {
			return valueCache.get(value);
		}
	}

	public static class ValueCache extends HashMap<Serializable, Serializable> {
		private static final long serialVersionUID = 8474952153415905986L;

		@Override
		public Serializable get(Object key) {
			Serializable result = super.get(key);
			if (result == null)
				put(result = (Serializable) key, result);
			return result;
		}
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
		return meta.equals(vertex.meta) && Objects.equals(value, vertex.value) && Arrays.equals(components, vertex.components);
	}

	public boolean inheritsFrom(Vertex superVertex) {
		if (this == superVertex || equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return Arrays.asList(supers).stream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	public boolean isInstanceOf(Vertex metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}

	public boolean inheritsFrom(Vertex superMeta, Serializable superValue, Vertex... superComponents) {
		return inheritsFrom(meta, value, components, superMeta, superValue, superComponents);
	}

	public boolean isSuperOf(Vertex subMeta, Serializable subValue, Vertex... subComponents) {
		return inheritsFrom(subMeta, subValue, subComponents, meta, value, components);
	}

	public static boolean inheritsFrom(Vertex subMeta, Serializable subValue, Vertex[] subComponents, Vertex superMeta, Serializable superValue, Vertex[] superComponents) {
		Inheritance inheritance = DEFAULT_INHERITANCE;
		return inheritance.inheritsFrom(subMeta, subValue, subComponents, superMeta, superValue, superComponents);
	}

	private final static Inheritance DEFAULT_INHERITANCE = new Inheritance();
	private final static Inheritance PROPERTY_INHERITANCE = new PropertyInheritance();

	private static class Inheritance {
		private final boolean inheritsFrom(Vertex subMeta, Serializable subValue, Vertex[] subComponents, Vertex superMeta, Serializable superValue, Vertex[] superComponents) {
			return metasInheritance().inheritsFrom(subMeta, superMeta) && componentsInheritance().inheritsFrom(subComponents, superComponents) && valuesInheritance().inheritsFrom(subValue, superValue);
		}

		protected MetasInheritance metasInheritance() {
			return DEFAULT_METAS_INHERITANCE;
		}

		protected ComponentsInheritance componentsInheritance() {
			return DEFAULT_COMPONENTS_INHERITANCE;
		}

		protected ValuesInheritance valuesInheritance() {
			return DEFAULT_VALUES_INHERITANCE;
		}
	}

	private static class PropertyInheritance extends Inheritance {
		@Override
		protected ValuesInheritance valuesInheritance() {
			return ALL_VALUES_INHERITANCE;
		}
	}

	private static class SingularInheritance extends Inheritance {
		@Override
		protected ComponentsInheritance componentsInheritance() {
			return SINGULAR_COMPONENTS_INHERITANCE;
		}

		@Override
		protected ValuesInheritance valuesInheritance() {
			return ALL_VALUES_INHERITANCE;
		}
	}

	private static class MapInheritance extends Inheritance {
		@Override
		protected ValuesInheritance valuesInheritance() {
			return MAP_VALUES_INHERITANCE;
		}
	}

	@FunctionalInterface
	public interface MetasInheritance {
		boolean inheritsFrom(Vertex subMeta, Vertex superMeta);
	}

	@FunctionalInterface
	public interface ComponentsInheritance {
		boolean inheritsFrom(Vertex[] subComponents, Vertex[] superComponents);
	}

	@FunctionalInterface
	public interface ValuesInheritance {
		boolean inheritsFrom(Serializable subValue, Serializable superValue);
	}

	private final static MetasInheritance DEFAULT_METAS_INHERITANCE = (subMeta, superMeta) -> subMeta.inheritsFrom(superMeta);

	private final static ComponentsInheritance DEFAULT_COMPONENTS_INHERITANCE = (subComponents, superComponents) -> componentsInherits(subComponents, superComponents);
	private final static ComponentsInheritance SINGULAR_COMPONENTS_INHERITANCE = (subComponents, superComponents) -> componentsInherits(subComponents, superComponents);

	private final static ValuesInheritance DEFAULT_VALUES_INHERITANCE = (subValue, superValue) -> Objects.equals(subValue, superValue);
	private final static ValuesInheritance ALL_VALUES_INHERITANCE = (subValue, superValue) -> true;
	private final static ValuesInheritance MAP_VALUES_INHERITANCE = (subValue, superValue) -> {
		return (subValue instanceof AbstractMap.SimpleImmutableEntry<?, ?> && superValue instanceof AbstractMap.SimpleImmutableEntry<?, ?>) ? Objects.equals(((Entry<?, ?>) subValue).getKey(), ((Entry<?, ?>) superValue).getKey()) : Objects.equals(subValue,
				superValue);
	};

	private static boolean componentsInherits(Vertex[] subComponents, Vertex[] superComponents) {
		int subIndex = 0;
		loop: for (int superIndex = 0; superIndex < superComponents.length; superIndex++) {
			Vertex superComponent = superComponents[superIndex];
			for (; subIndex < subComponents.length; subIndex++) {
				Vertex subComponent = subComponents[subIndex];
				if (subComponent.inheritsFrom(superComponent) || subComponent.isInstanceOf(superComponent)) {
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

	public Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(EMPTY_VERTICES, value, components);
	}

	public Vertex addInstance(Vertex[] supers, Serializable value, Vertex... components) {
		return new Vertex(this, supers, value, components).plug(true);
	}

	public Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(EMPTY_VERTICES, value, components);
	}

	public Vertex setInstance(Vertex[] supers, Serializable value, Vertex... components) {
		return new Vertex(this, supers, value, components).plug(false);
	}

	public Vertex getInstance(/* Vertex[] supers, */Serializable value, Vertex... components) {
		return new Vertex(this, value, components).getPlugged(false);
	}

	public boolean isStructural() {
		return getLevel() == 1;
	}

	public boolean isFactual() {
		return getLevel() == 2;
	}

	public Set<Vertex> getAttributes(Vertex attribute) {
		return getComposites().stream().filter(holder -> holder.isInstanceOf(attribute)).filter(holder -> holder.isStructural()).collect(Collectors.toSet());
	}

	public Set<Vertex> getHolders(Vertex attribute) {
		return getComposites().stream().filter(holder -> holder.isInstanceOf(attribute)).filter(holder -> holder.isFactual()).collect(Collectors.toSet());
	}

	public Set<Serializable> getValues(Vertex attribute) {
		return getComposites().stream().filter(holder -> holder.isInstanceOf(attribute)).filter(holder -> holder.isFactual()).map(vertex -> vertex.getValue()).collect(Collectors.toSet());
	}

	public boolean isPlugged() throws NotFoundException {
		return this == getPlugged(false);
	}

	public Vertex getPlugged(boolean throwNotFoundException) throws NotFoundException {
		Vertex result = meta.instances.get(this);
		if (result == null)
			if (throwNotFoundException)
				throw new ExistException();
		return result;
	}

	public String info() {
		return " (" + meta.value + "){" + this + "}" + Arrays.toString(supers) + Arrays.toString(components) + " ";
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}

	public class ExistException extends RuntimeException {
		private static final long serialVersionUID = -4631985293285253439L;

		public ExistException() {
			super("Vertex already exists : " + info());
		}
	}

	public class NotFoundException extends RuntimeException {

		private static final long serialVersionUID = -7472730943638836698L;

		public NotFoundException() {
			super("Vertex not found : " + info());
		}
	}

}
