package org.genericsystem.impl.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.value.BooleanValue;
import org.genericsystem.api.annotation.value.IntValue;
import org.genericsystem.api.annotation.value.StringValue;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.constraints.Constraint.CheckingType;
import org.genericsystem.impl.core.Statics.Primaries;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.iterator.AbstractSelectableLeafIterator;
import org.genericsystem.impl.snapshot.AbstractSnapshot;
import org.genericsystem.impl.system.ComponentPosValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 */
public abstract class AbstractContext implements Context, Serializable {

	protected static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	private static final long serialVersionUID = -6036571074310729022L;

	<T extends GenericImpl> T plug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : generic.components)
			if (componentSet.add(component))
				getCompositeDependencies(component).add(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : generic.supers)
			if (effectiveSupersSet.add(effectiveSuper))
				getDirectInheritingsDependencies(effectiveSuper).add(generic);
		return generic;
	}

	<T extends GenericImpl> T unplug(T generic) {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : generic.components)
			if (componentSet.add(component))
				getCompositeDependencies(component).remove(generic);
		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : generic.supers)
			if (effectiveSupersSet.add(effectiveSuper))
				getDirectInheritingsDependencies(effectiveSuper).remove(generic);
		return generic;
	}

	abstract TimestampedDependencies getDirectInheritingsDependencies(Generic effectiveSuper);

	abstract TimestampedDependencies getCompositeDependencies(Generic component);

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> compositesIterator(final Generic component) {
		return (Iterator<T>) getCompositeDependencies(component).iterator(getTs());
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Iterator<T> directInheritingsIterator(final Generic component) {
		return (Iterator<T>) getDirectInheritingsDependencies(component).iterator(getTs());
	}

	abstract InternalContext<? extends AbstractContext> getInternalContext();

	public abstract long getTs();

	public <T extends Attribute> T getMetaAttribute() {
		return getEngine().getMetaAttribute();
	}

	public <T extends Relation> T getMetaRelation() {
		return getEngine().getMetaRelation();
	}

	public abstract boolean isScheduledToAdd(Generic generic);

	public abstract boolean isScheduledToRemove(Generic generic);

	Iterator<Generic> getDirectSupersIterator(final Generic[] interfaces, final Generic[] components) {
		return new AbstractSelectableLeafIterator(this, getEngine()) {

			@Override
			protected boolean isSelectable() {
				return true;
			}

			@Override
			protected boolean isSelected(Generic father, Generic candidate) {
				return GenericImpl.isSuperOf(((GenericImpl) candidate).getPrimariesArray(), ((GenericImpl) candidate).components, interfaces, components);
			}
		};
	}

	protected Generic[] getDirectSupers(final Generic[] interfaces, final Generic[] components) {
		List<Generic> list = new ArrayList<Generic>();
		final Iterator<Generic> iterator = getDirectSupersIterator(interfaces, components);
		while (iterator.hasNext())
			list.add(iterator.next());
		// Generic[] result = list.toArray(new Generic[list.size()]);
		// assert Arrays.equals(new Primaries(result).toArray(), interfaces) :
		// new Primaries(result) + " <---> " + Arrays.toString(interfaces);
		return list.toArray(new Generic[list.size()]);
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T reFind(Generic generic) {
		if (generic.isAlive(this))
			return (T) generic;
		if (((GenericImpl) generic).isPrimary())
			return findPrimaryByValue(((GenericImpl) generic).supers[0], generic.getValue(), generic.getMetaLevel());
		Generic[] primariesArray = ((GenericImpl) generic).getPrimariesArray();
		Generic[] boundPrimaries = new Generic[primariesArray.length];
		for (int i = 0; i < primariesArray.length; i++)
			boundPrimaries[i] = reFind(((GenericImpl) primariesArray[i]));
		Generic[] extendedComponents = ((GenericImpl) generic).components;
		Generic[] extendedBoundComponents = new Generic[((GenericImpl) generic).components.length];
		for (int i = 0; i < extendedComponents.length; i++)
			extendedBoundComponents[i] = generic.equals(extendedComponents[i]) ? null : reFind(extendedComponents[i]);
		Generic[] directSupers = getDirectSupers(boundPrimaries, extendedBoundComponents);
		if (directSupers.length == 1 && ((GenericImpl) directSupers[0]).equiv(boundPrimaries, extendedBoundComponents))
			return (T) directSupers[0];
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T find(Generic[] supers, Generic[] components) {
		final Generic[] interfaces = new Primaries(supers).toArray();
		Generic[] directSupers = getDirectSupers(interfaces, components);
		if (directSupers.length == 1 && ((GenericImpl) directSupers[0]).equiv(interfaces, components))
			return (T) directSupers[0];
		return null;
	}

	<T extends Generic> NavigableSet<T> orderDependencies(final Generic generic) {
		return new TreeSet<T>() {
			private static final long serialVersionUID = 1053909994506452123L;
			{
				addDependencies(generic);
			}

			@SuppressWarnings("unchecked")
			public void addDependencies(Generic g) {
				if (super.add((T) g)) {// protect from loop
					for (T inheritingDependency : g.<T> getInheritings(AbstractContext.this))
						addDependencies(inheritingDependency);
					for (T compositeDependency : g.<T> getComposites(AbstractContext.this))
						addDependencies(compositeDependency);
				}
			}
		};
	}

	public abstract boolean isAlive(Generic generic);

	public <T extends Generic> T find(Class<?> clazz) {
		return this.<EngineImpl> getEngine().find(this, clazz);
	}

	<T extends Generic> T findMeta(Generic[] interfaces, Generic[] components) {
		for (T composite : getEngine().<T> getComposites(this))
			if (composite.isMeta() && Arrays.equals(interfaces, ((GenericImpl) composite).getPrimariesArray()) && Arrays.equals(components, ((GenericImpl) composite).components))
				return composite;
		return null;
	}

	Generic[] findSupers(Class<?> clazz) {
		int i = 0;
		Type[] supers = new Type[getSupersClasses(clazz).size()];
		for (Class<?> superClasse : getSupersClasses(clazz))
			supers[i++] = this.<Type> find(superClasse);
		return supers;
	}

	Generic findImplicitSuper(Class<?> clazz) {
		Supers supersAnnotation = clazz.getAnnotation(Supers.class);
		if (supersAnnotation == null) {
			Generic[] supers = findSupers(clazz);
			return supers.length == 0 ? getEngine() : supers[0].getImplicit();
		}
		return find(supersAnnotation.implicitSuper()).getImplicit();
	}

	int findMetaLevel(Class<?> clazz) {
		SystemGeneric annotation = clazz.getAnnotation(SystemGeneric.class);
		if (annotation == null)
			return SystemGeneric.STRUCTURAL;
		return annotation.value();
	}

	LinkedHashSet<Class<?>> getSupersClasses(Class<?> clazz) {
		Supers supersAnnotation = clazz.getAnnotation(Supers.class);
		LinkedHashSet<Class<?>> superClasses = new LinkedHashSet<>(Arrays.asList(supersAnnotation != null ? supersAnnotation.value() : new Class<?>[] {}));
		Class<?> javaSuperclass = clazz.getSuperclass();
		if (Object.class.equals(javaSuperclass))
			return superClasses;
		if (javaSuperclass.getAnnotation(SystemGeneric.class) == null)
			superClasses.addAll(getSupersClasses(javaSuperclass));
		else
			superClasses.add(javaSuperclass);
		return superClasses;
	}

	Generic[] findComponents(Class<?> clazz) {
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation == null)
			return Statics.EMPTY_GENERIC_ARRAY;
		Class<?>[] componentClasses = componentsAnnotation.value();
		Generic[] components = new Generic[componentClasses.length];
		for (int index = 0; index < componentClasses.length; index++)
			components[index] = find(componentClasses[index]);
		return components;
	}

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
		ComponentPosBoolean componentPosBoolean = clazz.getAnnotation(ComponentPosBoolean.class);
		if (componentPosBoolean != null)
			return new ComponentPosValue<Boolean>(componentPosBoolean.componentPos(), componentPosBoolean.value());
		return clazz;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T findPrimaryByValue(Generic primaryAncestor, Serializable value, int metaLevel) {
		assert metaLevel - primaryAncestor.getMetaLevel() <= 1;
		assert metaLevel - primaryAncestor.getMetaLevel() >= 0;
		Iterator<Generic> it = directInheritingsIterator(primaryAncestor);
		while (it.hasNext()) {
			Generic candidate = it.next();
			if (((GenericImpl) candidate).isPrimary() && (metaLevel == candidate.getMetaLevel()) && (Objects.hashCode(value) == Objects.hashCode(candidate.getValue())) && Objects.equals(value, candidate.getValue()))
				return (T) candidate;
		}
		return null;
	}

	public abstract class InternalContext<T extends AbstractContext> implements Serializable {

		private static final long serialVersionUID = 3961310676895965230L;

		@SuppressWarnings("unchecked")
		protected SortedSet<Constraint> getSortedConstraints(CheckingType checkingType, boolean immediatlyCheckable) {
			SortedSet<Constraint> sortedConstraints = new TreeSet<Constraint>();
			try {
				for (Generic constraint : getConstraints()) {
					Constraint constraintInstance = ((Class<? extends Constraint>) constraint.getValue()).newInstance();
					if (immediatlyCheckable == constraintInstance.isImmediatelyCheckable() && constraintInstance.isCheckedAt(checkingType))
						sortedConstraints.add(constraintInstance);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
			return sortedConstraints;
		}

		protected Snapshot<Generic> getConstraints() {
			return new AbstractSnapshot<Generic>() {

				@Override
				public Iterator<Generic> iterator() {
					return new AbstractFilterIterator<Generic>(directInheritingsIterator(getEngine())) {
						@Override
						public boolean isSelected() {
							return next.getValue() instanceof Class && Constraint.class.isAssignableFrom(((Class<?>) next.getValue()));
						}
					};
				}
			};
		}

		protected void checkConstraints(CheckingType checkingType, boolean immediatlyCheckable, Iterable<Generic> generics) throws ConstraintViolationException {
			for (Constraint constraint : getSortedConstraints(checkingType, immediatlyCheckable))
				for (Generic generic : generics)
					constraint.check(AbstractContext.this, generic);
		}

		@SuppressWarnings("unchecked")
		protected void checkConsistency(CheckingType checkingType, boolean immediatlyCheckable, Iterable<Generic> generics) throws ConstraintViolationException {
			for (Generic constraint : getConstraints()) {
				Constraint constraintInstance;
				try {
					constraintInstance = ((Class<? extends Constraint>) constraint.getValue()).newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
				if (constraintInstance.isCheckedAt(checkingType) && immediatlyCheckable == constraintInstance.isImmediatelyCheckable())
					for (Generic generic : generics)
						if (generic.isInstanceOf(constraint)) {
							Generic base = ((Holder) generic).getBaseComponent();
							if (base != null)
								for (Generic baseInheriting : ((GenericImpl) base).getAllInheritings(AbstractContext.this))
									constraintInstance.check(AbstractContext.this, baseInheriting);

						}
			}
		}

		protected void apply(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, ConstraintViolationException {
			removeAll(removes);
			addAll(adds);
			try {
				checkConstraints(adds, removes);
			} catch (RuntimeException e) {
				cancelAddAll(adds);
				cancelRemoveAll(removes);
				throw e;
			} catch (ConstraintViolationException e) {
				cancelAddAll(adds);
				cancelRemoveAll(removes);
				throw e;
			} catch (Exception e) {
				cancelAddAll(adds);
				cancelRemoveAll(removes);
				throw new IllegalStateException(e);
			}
		}

		protected void checkConstraints(Iterable<Generic> adds, Iterable<Generic> removes) throws ConstraintViolationException {
			checkConsistency(CheckingType.CHECK_ON_ADD_NODE, false, adds);
			checkConsistency(CheckingType.CHECK_ON_REMOVE_NODE, false, removes);
			checkConstraints(CheckingType.CHECK_ON_ADD_NODE, false, adds);
			checkConstraints(CheckingType.CHECK_ON_REMOVE_NODE, false, removes);
		}

		private void addAll(Iterable<Generic> generics) {
			for (Generic generic : generics)
				add((GenericImpl) generic);
		}

		private void removeAll(Iterable<Generic> generics) {
			for (Generic generic : generics)
				remove((GenericImpl) generic);
		}

		private void cancelAddAll(Iterable<Generic> generics) {
			for (Generic generic : generics)
				cancelAdd((GenericImpl) generic);
		}

		private void cancelRemoveAll(Iterable<Generic> generics) {
			for (Generic generic : generics)
				cancelRemove((GenericImpl) generic);
		}

		protected void add(GenericImpl generic) {
			plug(generic);
		}

		protected void remove(GenericImpl generic) {
			unplug(generic);
		}

		protected void cancelAdd(GenericImpl generic) {
			unplug(generic);
		}

		protected void cancelRemove(GenericImpl generic) {
			plug(generic);
		}
	}

	public interface TimestampedDependencies {

		void add(Generic generic);

		void remove(Generic generic);

		Iterator<Generic> iterator(long ts);
	}

}
