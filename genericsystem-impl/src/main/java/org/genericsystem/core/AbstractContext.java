package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.map.AxedPropertyClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractContext implements Serializable {

	protected static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	private static final long serialVersionUID = -6036571074310729022L;

	abstract <T extends Engine> T getEngine();

	<T extends Generic> T plug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : ((GenericImpl) generic).components)
			if (componentSet.add(component))
				getCompositeDependencies(component).add(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).supers)
			if (effectiveSupersSet.add(effectiveSuper))
				getDirectInheritingsDependencies(effectiveSuper).add(generic);
		return generic;
	}

	<T extends Generic> T unplug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : ((GenericImpl) generic).components)
			if (componentSet.add(component))
				getCompositeDependencies(component).remove(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : ((GenericImpl) generic).supers)
			if (effectiveSupersSet.add(effectiveSuper))
				getDirectInheritingsDependencies(effectiveSuper).remove(generic);
		return generic;
	}

	abstract TimestampedDependencies getDirectInheritingsDependencies(Generic effectiveSuper);

	abstract TimestampedDependencies getCompositeDependencies(Generic component);

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> compositesIterator(Generic component) {
		return (Iterator<T>) getCompositeDependencies(component).iterator(getTs());
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> directInheritingsIterator(Generic component) {
		return (Iterator<T>) getDirectInheritingsDependencies(component).iterator(getTs());
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

	@SuppressWarnings("unchecked")
	public <T extends Generic> T reFind(Generic generic) {
		if (generic.isEngine() || generic.isAlive())
			return (T) generic;
		// TODO KK : we have to call GenericBuilder.find() here
		return fastFindBySuper(((GenericImpl) generic).homeTreeNode, reFind(((GenericImpl) generic).supers), reFind(((GenericImpl) generic).components));
	}

	private Generic[] reFind(Generic... generics) {
		Generic[] reFounds = new Generic[generics.length];
		for (int i = 0; i < generics.length; i++)
			reFounds[i] = reFind(generics[i]);
		// TODO KK : if refind is null => exit caller method with null
		return reFounds;
	}

	// TODO KK : Remove this method
	@SuppressWarnings("unchecked")
	<T extends Generic> T fastFindBySuper(HomeTreeNode homeTreeNode, Generic[] supers, Generic[] components) {
		for (Generic generic : components.length == 0 || components[0] == null ? supers[0].getInheritings() : components[0].getComposites())
			if (((GenericImpl) generic).equiv(homeTreeNode, components))// // TODO KK : Remove this method
				return (T) generic;
		return null;
	}

	<T extends Generic> NavigableSet<T> orderDependenciesForRemove(final Generic generic) throws ReferentialIntegrityConstraintViolationException {
		return new TreeSet<T>() {
			private static final long serialVersionUID = -6526972335865898198L;
			{
				addDependencies(generic);
			}

			@SuppressWarnings("unchecked")
			public void addDependencies(Generic generic) throws ReferentialIntegrityConstraintViolationException {
				if (super.add((T) generic)) {// protect from loop
					for (T inheritingDependency : generic.<T> getInheritings())
						// TODO clean
						if (/* ((GenericImpl) inheritingDependency).isPhantom() || */((GenericImpl) inheritingDependency).isAutomatic())
							addDependencies(inheritingDependency);
						else if (!contains(inheritingDependency))
							throw new ReferentialIntegrityConstraintViolationException(inheritingDependency + " is an inheritance dependency for ancestor " + generic);
					for (T compositeDependency : generic.<T> getComposites())
						if (!generic.equals(compositeDependency)) {
							for (int componentPos = 0; componentPos < ((GenericImpl) compositeDependency).components.length; componentPos++)
								if (!((GenericImpl) compositeDependency).isAutomatic() && ((GenericImpl) compositeDependency).components[componentPos].equals(generic) && !contains(compositeDependency)
										&& compositeDependency.isReferentialIntegrity(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(compositeDependency + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							addDependencies(compositeDependency);
						}
					for (int axe = 0; axe < ((GenericImpl) generic).components.length; axe++)
						if (((GenericImpl) generic).isCascadeRemove(axe))
							addDependencies(((GenericImpl) generic).components[axe]);
				}
			}
		};
	}

	NavigableSet<Generic> orderDependencies(final Generic generic) {
		return new TreeSet<Generic>() {
			private static final long serialVersionUID = 1053909994506452123L;
			{
				if (generic.isAlive())
					addDependencies(generic);
			}

			public void addDependencies(Generic dependency) {
				if (super.add(dependency)) {// protect from loop
					for (Generic inheritingDependency : dependency.<Generic> getInheritings())
						addDependencies(inheritingDependency);
					for (Generic compositeDependency : dependency.<Generic> getComposites())
						addDependencies(compositeDependency);
				}
			}
		};
	}

	NavigableMap<Generic, Integer> orderDependencyMap(final Generic generic, final int basePos) {
		return new TreeMap<Generic, Integer>() {
			private static final long serialVersionUID = 1053909994506452123L;
			{
				if (generic.isAlive())
					addDependencies(generic, basePos);
			}

			public void addDependencies(Generic generic, int basePos) {
				if (!super.containsKey(generic)) {// protect from loop
					put(generic, basePos);
					for (Generic inheriting : generic.getInheritings())
						addDependencies(inheriting, getInheritingPosition(inheriting, generic, basePos));
					for (Generic composite : generic.getComposites())
						addDependencies(composite, getCompositePosition(generic, (Holder) composite));
				}
			}

			private int getInheritingPosition(Generic inheriting, Generic generic, int basePos) {
				if (Statics.MULTIDIRECTIONAL == basePos)
					return basePos;
				if (inheriting.getComponentsSize() == ((GenericImpl) generic).components.length)
					return basePos;
				for (int i = basePos; i < inheriting.getComponentsSize(); i++)
					if (generic.inheritsFrom(((Holder) inheriting).getComponent(i)))
						return i;
				return Statics.MULTIDIRECTIONAL;
			}

			private int getCompositePosition(Generic generic, Holder composite) {
				for (int i = 0; i < composite.getComponentsSize(); i++)
					if (generic.equals(composite.getComponent(i)))
						return i;
				throw new IllegalStateException();
			}
		};
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

	// @SuppressWarnings("unchecked")
	// public <T extends Generic> T findPrimaryByValue(Generic meta, Serializable value) {
	// Iterator<Generic> it = directInheritingsIterator(meta);
	// while (it.hasNext()) {
	// Generic candidate = it.next();
	// if (((GenericImpl) candidate).isPrimary() && (Objects.hashCode(value) == Objects.hashCode(candidate.getValue())) && Objects.equals(value, candidate.getValue()))
	// return (T) candidate;
	// }
	// return null;
	// }

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

	interface TimestampedDependencies {

		void add(Generic generic);

		void remove(Generic generic);

		Iterator<Generic> iterator(long ts);
	}

}
