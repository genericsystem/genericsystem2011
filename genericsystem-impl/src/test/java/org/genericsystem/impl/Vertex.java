package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

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

	private Vertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		this.meta = isEngine() ? this : meta;
		this.value = isEngine() ? value : getEngine().getCachedValue(value);
		this.components = components;
		this.supers = new SupersComputer(overrides).toArray();
		assert Arrays.asList(overrides).stream().allMatch(override -> Arrays.asList(supers).stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))) : "Inconsistant overrides : " + Arrays.toString(overrides) + Arrays.toString(supers);
		assert componentsDepends(components, this.meta.components) : "Inconsistant components : " + Arrays.toString(components);
		assert Arrays.asList(supers).stream().allMatch(superVertex -> meta.inheritsFrom(superVertex.getMeta())) : "Inconsistant supers : " + Arrays.toString(supers);
		// assert Arrays.asList(supers).stream().allMatch(superVertex -> componentsDepends(components, superVertex.components)) : "Inconsistant supers : " + Arrays.toString(supers);
		assert Arrays.asList(supers).stream().filter(superVertex -> superVertex.inheritsFrom(meta)).count() <= 1 : "Inconsistant supers : " + Arrays.toString(supers);
		assert Arrays.asList(supers).stream().noneMatch(superVertex -> Objects.equals(superVertex, this)) : "Inconsistant supers : " + Arrays.toString(supers);
		assert Arrays.asList(supers).stream().filter(superVertex -> superVertex.isInstanceOf(meta)).count() < 2 : "Inconsistant supers : " + Arrays.toString(supers);
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
		Arrays.asList(supers).forEach(superGeneric -> putThisIfAbsentOfIndex(superGeneric.inheritings, throwExistException));
		Arrays.asList(components).forEach(component -> putThisIfAbsentOfIndex(component.composites, throwExistException));
		return putThisIfAbsentOfIndex(meta.instances, throwExistException);
	}

	private Vertex putThisIfAbsentOfIndex(Map<Vertex, Vertex> index, boolean throwExistException) throws ExistException {
		if (!index.containsKey(this)) {
			Vertex result = index.put(this, this);
			assert result == null; // TODO if result != null ?
			return this;
		}
		if (throwExistException)
			throw new ExistException();
		return index.get(this);
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
		return equals(vertex.getMeta(), vertex.getValue(), vertex.getComponents());
	}

	public boolean equals(Vertex meta, Serializable value, Vertex... components) {
		return this.meta.equals(meta) && Objects.equals(this.value, value) && Arrays.equals(this.components, components);
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

	public boolean isSuperOf(Vertex subMeta, Vertex[] overrides, Serializable subValue, Vertex... subComponents) {
		return Arrays.asList(overrides).stream().anyMatch(override -> override.inheritsFrom(this)) || inheritsFrom(subMeta, subValue, subComponents, meta, value, components);
	}

	public static boolean inheritsFrom(Vertex subMeta, Serializable subValue, Vertex[] subComponents, Vertex superMeta, Serializable superValue, Vertex[] superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		int subIndex = 0;
		loop: for (int superIndex = 0; superIndex < superComponents.length; superIndex++) {
			Vertex superComponent = superComponents[superIndex];
			for (; subIndex < subComponents.length; subIndex++) {
				Vertex subComponent = subComponents[subIndex];
				if (subComponent.inheritsFrom(superComponent) || subComponent.isInstanceOf(superComponent)) {
					if (subMeta.getSingulars()[subIndex])
						return true;
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return subMeta.isProperty() || Objects.equals(subValue, superValue);
	}

	private boolean property = false;
	private boolean[] singulars = new boolean[/* components.length */10];

	private boolean isProperty() {
		return property;
	}

	private boolean[] getSingulars() {
		return singulars;
	}

	private static boolean componentsDepends(Vertex[] subComponents, Vertex[] superComponents) {
		log.info("ZZZZZZZZ" + Arrays.toString(subComponents) + Arrays.toString(superComponents));
		int subIndex = 0;
		loop: for (int superIndex = 0; superIndex < superComponents.length; superIndex++) {
			Vertex superComponent = superComponents[superIndex];
			for (; subIndex < subComponents.length; subIndex++) {
				Vertex subComponent = subComponents[subIndex];
				log.info(subComponent + " inheritsFrom " + superComponent);
				if (subComponent.inheritsFrom(superComponent) || subComponent.isInstanceOf(superComponent)) {
					log.info(subComponent + " inheritsFrom " + superComponent + " true");
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		log.info("Return true");
		return true;
	}

	public Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(EMPTY_VERTICES, value, components);
	}

	public Vertex addInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return new Vertex(this, overrides, value, components).plug(true);
	}

	public Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(EMPTY_VERTICES, value, components);
	}

	public Vertex setInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		Vertex result = new Vertex(this, overrides, value, components).plug(false);
		assert Arrays.asList(overrides).stream().allMatch(override -> Arrays.asList(result.supers).stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))) : "Result : " + result.info() + " don't satisfy overrides : "
				+ Arrays.toString(overrides);
		return result;
	}

	private class SupersComputer extends LinkedHashSet<Vertex> {

		private static final long serialVersionUID = -1078004898524170057L;

		private final Vertex[] overrides;
		private final Map<Vertex, Boolean> alreadyComputed = new HashMap<>();

		private SupersComputer(Vertex[] overrides) {
			this.overrides = overrides;
			isSelected(meta.getEngine());
		}

		private boolean isSelected(Vertex candidate) {
			Boolean result = alreadyComputed.get(candidate);
			if (result != null)
				return result;
			log.info("AAAAAAAAA" + candidate.info());
			if (!meta.inheritsFrom(candidate.getMeta()) && !meta.isInstanceOf(candidate.getMeta())) {
				alreadyComputed.put(candidate, false);
				return false;
			}
			log.info("BBBBBBBBB" + candidate.info());
			if (candidate.getLevel() <= meta.getLevel())
				for (Vertex instance : candidate.getInstances())
					if (isSelected(instance)) {
						alreadyComputed.put(candidate, false);
						return false;
					}
			log.info("CCCCCCCCCCCCCC" + candidate.info());
			for (Vertex inheriting : candidate.getInheritings())
				if (isSelected(inheriting)) {
					alreadyComputed.put(candidate, false);
					return false;
				}
			log.info("CCCCCCCCCCCCCC2" + candidate.info());
			if (!candidate.isSuperOf(meta, overrides, value, components)) {
				alreadyComputed.put(candidate, false);
				return false;
			}

			log.info("DDDDDDDDDDDD" + candidate.info());
			if (candidate.equals(meta, value, components)) {
				alreadyComputed.put(candidate, false);
				return false;
			}
			log.info("EEEEEEEEEEE" + candidate.info());
			add(candidate);
			alreadyComputed.put(candidate, true);
			return true;
		}

		@Override
		public Vertex[] toArray() {
			return toArray(new Vertex[size()]);
		}

		@Override
		public boolean add(Vertex candidate) {
			for (Vertex vertex : this) {
				if (vertex.equals(candidate)) {
					assert false : "Candidate already exists : " + candidate.info();
				} else if (vertex.inheritsFrom(candidate)) {
					assert false : vertex.info() + candidate.info();
				}
			}
			Iterator<Vertex> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next())) {
					assert false;
					it.remove();
				}
			boolean result = super.add(candidate);
			assert result;
			return true;
		}
	}

	public Vertex getInstance(/* Vertex[] supers, */Serializable value, Vertex... components) {
		return new Vertex(this, value, components).getPlugged(false);
	}

	public boolean isMeta() {
		return getLevel() == 0;
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

	@SuppressWarnings({ "unchecked" })
	private <T extends Vertex> Iterator<T> compositesIterator(final Vertex meta) {
		return new AbstractFilterIterator<T>((Iterator<T>) getComposites().iterator()) {
			@Override
			public boolean isSelected() {
				return next.getMeta().equals(meta);
			}
		};
	}

	// private class MainInheritanceIterator {
	//
	// private static final long serialVersionUID = 2189650244025973386L;
	//
	// private <T extends Vertex> Iterator<T> iterator(Vertex origin) {
	// return projectIterator(projectFromMetaIterator(origin));
	// }
	//
	// private Iterator<Vertex> projectFromMetaIterator(Vertex origin) {
	// Iterator<Vertex> mainMetaIterator = meta.new MainInheritanceIterator().iterator(origin);
	// if (!mainMetaIterator.hasNext())
	// return meta.composites.containsKey(origin) ? new SingletonIterator<Vertex>(origin) : Collections.emptyIterator();
	// return new AbstractConcateIterator<Vertex, Vertex>(mainMetaIterator) {
	//
	// @Override
	// protected Iterator<Vertex> getIterator(Vertex attributeOfMeta) {
	// return compositesIterator(attributeOfMeta);
	// }
	//
	// };
	// }
	// }

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
