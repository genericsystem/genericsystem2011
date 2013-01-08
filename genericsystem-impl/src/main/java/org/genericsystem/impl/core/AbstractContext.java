package org.genericsystem.impl.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.IntValue;
import org.genericsystem.api.annotation.Interfaces;
import org.genericsystem.api.annotation.PhantomValue;
import org.genericsystem.api.annotation.StringValue;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.constraints.Constraint.CheckingType;
import org.genericsystem.impl.core.Statics.Primaries;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.iterator.AbstractMagicIterator;
import org.genericsystem.impl.snapshot.AbstractSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
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
		for (Generic effectiveSuper : generic.directSupers)
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
		for (Generic effectiveSuper : generic.directSupers)
			if (effectiveSupersSet.add(effectiveSuper))
				getDirectInheritingsDependencies(effectiveSuper).remove(generic);
		return generic;
	}
	
	abstract TimestampedDependencies getDirectInheritingsDependencies(Generic effectiveSuper);
	
	abstract TimestampedDependencies getCompositeDependencies(Generic component);
	
	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> compositesIterator(final Generic component) {
		return (Iterator<T>) getCompositeDependencies(component).iterator(getTs());
	}
	
	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> directInheritingsIterator(final Generic component) {
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
		return new AbstractMagicIterator(this, getEngine()) {
			
			@Override
			protected boolean isSelectable() {
				return true;
			}
			
			@Override
			protected boolean isSelected(Generic candidate) {
				boolean result = GenericImpl.isSuperOf(((GenericImpl) candidate).isPrimary() ? new Generic[] { (GenericImpl) candidate } : ((GenericImpl) candidate).interfaces, ((GenericImpl) candidate).components, interfaces, components);
				// log.info("super :"+Arrays.toString(((GenericImpl)candidate).interfaces)+Arrays.toString(((GenericImpl)candidate).components));
				// log.info("sub"+Arrays.toString(interfaces)+Arrays.toString(components));
				// log.info("result selected : "+result);
				return result;
			}
		};
	}
	
	protected Generic[] getDirectSupers(final Generic[] interfaces, final Generic[] components) {
		List<Generic> list = new ArrayList<Generic>() {
			private static final long serialVersionUID = 3578292736549817796L;
			{
				final Iterator<Generic> iterator = getDirectSupersIterator(interfaces, components);
				while (iterator.hasNext()) {
					Generic g = iterator.next();
					// log.info("G : "+g);
					add(g);
				}
			}
		};
		return list.toArray(new Generic[list.size()]);
	}
	
	// @SuppressWarnings("unchecked")
	// public <T extends Generic> T find(Generic[] directSupers, Generic...
	// components) {
	// assert directSupers.length != 0 || components.length != 0;
	// if (directSupers.length == 0 && components.length == 1) {
	// return (T) components[0];// not possible !
	// }
	// if (directSupers.length == 1 && components.length == 0) {
	// return (T) directSupers[0];
	// }
	// if (directSupers.length == 1 && components.length == 1) {
	// if (directSupers[0].isEngine() && getEngine().equals(components[0]))
	// return getMetaAttribute();
	// }
	// if (directSupers.length == 1 && components.length == 2) {
	// if (directSupers[0].isEngine() && getEngine().equals(components[0]) &&
	// getEngine().equals(components[1]))
	// return getMetaRelation();
	// }
	// T result = this.<T>internalFind(directSupers, components);
	// //log.info(Arrays.toString(directSupers)+Arrays.toString(components)+" result : "+result);
	// return result;
	//
	// }
	
	private static Generic[] transform(Generic[] components, Generic generic) {
		Generic[] result = components.clone();
		for (int i = 0; i < result.length; i++)
			if (result[i] == null)
				result[i] = generic;
		return result;
	}
	
	@SuppressWarnings("unchecked")
	<T extends Generic> T find(Generic[] directSupers, Generic[] components) {
		Iterator<Generic> iterator = components.length > 0 && components[0] != null ? compositesIterator(components[0]) : directInheritingsIterator(directSupers[0]);
		while (iterator.hasNext()) {
			Generic directInheriting = iterator.next();
			if (Arrays.equals(((GenericImpl) directInheriting).directSupers, directSupers) && Arrays.equals(((GenericImpl) directInheriting).components, transform(components, directInheriting)))
				return (T) directInheriting;
		}
		return null;
	}
	
	public abstract boolean isAlive(Generic generic);
	
	public <T extends Generic> T find(Class<?> clazz) {
		// log.info(""+clazz);
		return this.<EngineImpl> getEngine().find(this, clazz);
	}
	
	// <T extends Generic> T internalFind(Class<?> clazz) {
	// checkSystemGenericClass(clazz);
	// Generic[] annotedInterfaces = findAnnotedInterfaces(clazz);
	// Generic implicit =
	// findPrimaryByValue(getSuperToCheck(annotedInterfaces).getImplicit(),
	// getImplictValue(clazz),
	// clazz.getAnnotation(SystemGeneric.class).value());
	// if (implicit == null)
	// return null;
	// return find(Statics.insertFirstIntoArray(implicit,
	// orderInterfaces(getInterfaces(annotedInterfaces))),
	// findComponents(clazz));
	// }
	
	protected void checkSystemGenericClass(Class<?> clazz) {
		SystemGeneric systemGeneric = clazz.getAnnotation(SystemGeneric.class);
		if (systemGeneric == null)
			throw new IllegalStateException("Unable to provide non annoted @SystemGeneric class : " + clazz);
	}
	
	static Generic[] orderInterfaces(final Generic[] interfaces) {
		return new Primaries(interfaces).toArray();
		
		// Set<Generic> adjusted = new TreeSet<Generic>() {
		// private static final long serialVersionUID = 4439816099896120671L;
		// {
		// for (Generic candidate : interfaces) {
		// boolean toAdd = true;
		// for (Generic generic : this)
		// if (generic.inheritsFrom(candidate)) {
		// toAdd = false;
		// break;
		// }
		// if (toAdd)
		// add(candidate);
		// }
		// }
		// };
		// return adjusted.toArray(new Generic[adjusted.size()]);
	}
	
	Generic getSuperToCheck(Generic[] annotedInterfaces) {
		if (annotedInterfaces.length == 0)
			return getEngine();
		if (annotedInterfaces.length == 1)
			return annotedInterfaces[0];
		return getEngine();
	}
	
	static Generic[] getInterfaces(Generic[] annotedInterfaces) {
		if (annotedInterfaces.length == 0)
			return Statics.EMPTY_GENERIC_ARRAY;
		if (annotedInterfaces.length == 1)
			return Statics.truncate(0, ((GenericImpl) annotedInterfaces[0]).interfaces);
		return annotedInterfaces;
	}
	
	<T extends Generic> T findMeta(Generic[] interfaces, Generic[] components) {
		for (T composite : getEngine().<T> getComposites(this))
			if (composite.isMeta() && Arrays.equals(interfaces, ((GenericImpl) composite).interfaces) && Arrays.equals(components, ((GenericImpl) composite).components))
				return composite;
		return null;
	}
	
	protected Generic[] findAnnotedInterfaces(Class<?> clazz) {
		LinkedHashSet<Class<?>> interfacesClasses = getAdditionalInterfaceClasses(clazz);
		Type[] interfaces = new Type[interfacesClasses.size()];
		int i = 0;
		for (Class<?> interfacesClasse : interfacesClasses)
			interfaces[i++] = this.<Type> find(interfacesClasse);
		return interfaces;
	}
	
	protected Generic[] findComponents(Class<?> clazz) {
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation == null)
			return Statics.EMPTY_GENERIC_ARRAY;
		Class<?>[] componentClasses = componentsAnnotation.value();
		Generic[] components = new Generic[componentClasses.length];
		for (int index = 0; index < componentClasses.length; index++)
			components[index] = find(componentClasses[index]);
		return components;
	}
	
	protected LinkedHashSet<Class<?>> getAdditionalInterfaceClasses(Class<?> clazz) {
		Interfaces interfacesAnnotation = clazz.getAnnotation(Interfaces.class);
		LinkedHashSet<Class<?>> interfaceClasses = new LinkedHashSet<>(Arrays.asList(interfacesAnnotation != null ? interfacesAnnotation.value() : new Class<?>[] {}));
		Class<?> javaSuperclass = clazz.getSuperclass();
		assert javaSuperclass != null : clazz;
		if (Object.class.equals(javaSuperclass))
			return interfaceClasses;
		if (javaSuperclass.getAnnotation(SystemGeneric.class) == null)
			interfaceClasses.addAll(getAdditionalInterfaceClasses(javaSuperclass));
		else
			interfaceClasses.add(javaSuperclass);
		return interfaceClasses;
	}
	
	protected static Serializable getImplictValue(Class<?> clazz) {
		BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
		if (booleanValue != null)
			return booleanValue.value();
		IntValue intValue = clazz.getAnnotation(IntValue.class);
		if (intValue != null)
			return intValue.value();
		StringValue stringValue = clazz.getAnnotation(StringValue.class);
		if (stringValue != null)
			return stringValue.value();
		if (clazz.getAnnotation(PhantomValue.class) != null)
			return Statics.PHAMTOM;
		return clazz;
	}
	
	protected Generic getImplicitSuper(Type[] interfaces) {
		return interfaces.length == 1 ? interfaces[0].getImplicit() : getEngine();
	}
	
	@SuppressWarnings("unchecked")
	<T extends Generic> T findPrimaryByValue(Generic primaryAncestor, Serializable value, int metaLevel) {
		assert metaLevel - primaryAncestor.getMetaLevel() <= 1;
		assert metaLevel - primaryAncestor.getMetaLevel() >= 0;
		Iterator<Generic> it = directInheritingsIterator(primaryAncestor);
		while (it.hasNext()) {
			Generic candidate = it.next();
			if (((GenericImpl) candidate).isPrimary() && (metaLevel == candidate.getMetaLevel()) && Objects.equals(value, candidate.getValue()))
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
							Generic base = ((Value) generic).getBaseComponent();
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
