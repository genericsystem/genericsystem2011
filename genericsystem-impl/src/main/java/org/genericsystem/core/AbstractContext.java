package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Statics.OrderedDependencies;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.map.AxedPropertyClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractContext {

	protected static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	abstract <T extends Engine> T getEngine();

	public <T extends Generic> T plug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : ((GenericImpl) generic).getComponents())
			if (componentSet.add(component))
				getComposites(component).add(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).getSupers())
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritingsAndInstances(effectiveSuper).add(generic);
		effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).getStrictSupers())
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritings(effectiveSuper).add(generic);
		getInstances(generic.getMeta()).add(generic);
		return generic;
	}

	<T extends Generic> T unplug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : ((GenericImpl) generic).getComponents())
			if (componentSet.add(component))
				getComposites(component).remove(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).getSupers())
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritingsAndInstances(effectiveSuper).remove(generic);
		effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).getStrictSupers())
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritings(effectiveSuper).remove(generic);

		getInstances(generic.getMeta()).remove(generic);
		return generic;
	}

	@Deprecated
	abstract TimestampedDependencies getInheritingsAndInstances(Generic effectiveSuper);

	abstract TimestampedDependencies getInstances(Generic meta);

	abstract TimestampedDependencies getInheritings(Generic strictSuper);

	abstract TimestampedDependencies getComposites(Generic component);

	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends Generic> Iterator<T> inheritingsAndInstancesIterator(Generic effectiveSuper) {
		return (Iterator<T>) getInheritingsAndInstances(effectiveSuper).iterator(getTs());
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> inheritingsIterator(Generic meta) {
		return (Iterator<T>) getInheritings(meta).iterator(getTs());
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> instancesIterator(Generic meta) {
		return (Iterator<T>) getInstances(meta).iterator(getTs());
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> compositesIterator(Generic component) {
		return (Iterator<T>) getComposites(component).iterator(getTs());
	}

	public abstract long getTs();

	public <T extends Attribute> T getMetaAttribute() {
		return getEngine().getMetaAttribute();
	}

	public <T extends Relation> T getMetaRelation() {
		return getEngine().getMetaRelation();
	}

	public abstract boolean isScheduledToAdd(Generic generic);

	public abstract boolean isScheduledToRemove(Generic generic);

	public abstract boolean isAutomatic(Generic generic);

	// TODO move this in GenericImpl
	<T extends Generic> NavigableSet<T> orderDependenciesForRemove(final Generic generic) throws ReferentialIntegrityConstraintViolationException {
		return new TreeSet<T>() {
			private static final long serialVersionUID = -6526972335865898198L;
			{
				addDependencies(generic);
			}

			@SuppressWarnings("unchecked")
			public void addDependencies(Generic generic) throws ReferentialIntegrityConstraintViolationException {
				if (super.add((T) generic)) {// protect from loop
					for (T inheritingDependency : generic.<T> getInheritingsAndInstances())
						if (((GenericImpl) inheritingDependency).isAutomatic())
							addDependencies(inheritingDependency);
						else if (!contains(inheritingDependency))
							throw new ReferentialIntegrityConstraintViolationException(inheritingDependency + " is an inheritance dependency for ancestor " + generic);
					for (T compositeDependency : generic.<T> getComposites())
						if (!generic.equals(compositeDependency)) {
							for (int componentPos = 0; componentPos < ((GenericImpl) compositeDependency).getComponents().size(); componentPos++)
								if (!((GenericImpl) compositeDependency).isAutomatic() && ((GenericImpl) compositeDependency).getComponent(componentPos).equals(generic) && !contains(compositeDependency)
										&& compositeDependency.isReferentialIntegrity(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(compositeDependency + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							addDependencies(compositeDependency);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (((GenericImpl) generic).isCascadeRemove(axe))
							addDependencies(((GenericImpl) generic).getComponent(axe));
				}
			}
		};
	}

	// TODO move this in GenericImpl
	static NavigableSet<Generic> orderDependencies(final Generic generic) {
		OrderedDependencies dependencies = new OrderedDependencies();
		if (generic.isAlive()) // KK ?
			dependencies.addDependencies(generic);
		return dependencies;
	}

	public abstract boolean isAlive(Generic generic);

	public <T extends Generic> T find(Class<?> clazz) {
		return this.<EngineImpl> getEngine().find(clazz);
	}

	Generic[] findUserSupers(Class<?> clazz) {
		int i = 0;
		LinkedHashSet<Class<?>> supersClasses = getSupersClasses(clazz);
		Type[] supers = new Type[supersClasses.size()];
		for (Class<?> superClasse : supersClasses)
			supers[i++] = this.<Type> find(superClasse);
		return supers;
	}

	LinkedHashSet<Class<?>> getSupersClasses(Class<?> clazz) {
		Extends extendsAnnotation = clazz.getAnnotation(Extends.class);
		LinkedHashSet<Class<?>> extendsClasses = new LinkedHashSet<>();
		if (extendsAnnotation != null) {
			extendsClasses.addAll(Arrays.asList(extendsAnnotation.value()));
			return extendsClasses;
		}
		Class<?> javaSuperclass = clazz.getSuperclass();
		if (Object.class.equals(javaSuperclass))
			return extendsClasses;
		extendsClasses.addAll(javaSuperclass.getAnnotation(SystemGeneric.class) == null ? getSupersClasses(javaSuperclass) : Arrays.asList(javaSuperclass));
		return extendsClasses;
	}

	Generic[] findComponents(Class<?> clazz) {
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation == null)
			return Statics.EMPTY_GENERIC_ARRAY;
		Class<?>[] componentClasses = componentsAnnotation.value();
		Generic[] components = new Generic[componentClasses.length];
		for (int index = 0; index < componentClasses.length; index++)
			components[index] = !clazz.equals(componentClasses[index]) ? find(componentClasses[index]) : null;
		return components;
	}

	@SuppressWarnings("unchecked")
	protected static Serializable findImplictValue(Class<?> clazz) {
		BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
		if (booleanValue != null)
			return booleanValue.value();
		IntValue intValue = clazz.getAnnotation(IntValue.class);
		if (intValue != null)
			return intValue.value();
		StringValue stringValue = clazz.getAnnotation(StringValue.class);
		if (stringValue != null)
			return stringValue.value();
		AxedConstraintValue axedConstraintValue = clazz.getAnnotation(AxedConstraintValue.class);
		if (axedConstraintValue != null)
			return new AxedPropertyClass((Class<GenericImpl>) axedConstraintValue.value(), axedConstraintValue.axe());
		return clazz;
	}

	void apply(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	final void addAll(Iterable<Generic> generics) {
		for (Generic generic : generics)
			simpleAdd(generic);
	}

	void removeAll(Iterable<Generic> generics) {
		for (Generic generic : generics)
			simpleRemove(generic);
	}

	void simpleAdd(Generic generic) {
		plug(generic);
	}

	void simpleRemove(Generic generic) {
		unplug(generic);
	}

	abstract int getLevel();

	abstract Generic searchByDesignTs(long ts);

	interface TimestampedDependencies {

		void add(Generic generic);

		void remove(Generic generic);

		Iterator<Generic> iterator(long ts);
	}

}
