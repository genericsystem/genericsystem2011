//package org.genericsystem.core;
//
//import java.util.HashMap;
//import java.util.NavigableSet;
//import java.util.TreeSet;
//import org.genericsystem.core.Statics.Primaries;
//import org.genericsystem.exception.ConstraintViolationException;
//
//abstract class Organizer extends HashMap<Generic, Generic> {
//	@SuppressWarnings("unchecked")
//	<T extends Generic> T doWork(Generic old, boolean computeDirectSupers) {
//		clear();
//		NavigableSet<Generic> dependencies = collectDependencies(old);
//		dependencies.add(old);
//		for (Generic oldDependency : dependencies.descendingSet())
//			simpleRemove(oldDependency);
//		for (Generic oldDependency : dependencies) {
//			Generic newDependency = old == oldDependency ? rebuild(old) : rebuildDependency(oldDependency, computeDirectSupers);
//			simpleAdd(newDependency);
//			put(oldDependency, newDependency);
//		}
//		try {
//			checkConstraints();
//		} catch (ConstraintViolationException e) {
//			rollback(e);
//		}
//		return (T) get(old);
//	}
//
//	abstract NavigableSet<Generic> collectDependencies(Generic old);
//
//	abstract <T extends Generic> T rebuild(Generic old);
//
//	Generic rebuildDependency(Generic oldDependency, boolean computeDirectSupers) {
//		if (((GenericImpl) oldDependency).isPrimary()) {
//			Generic primaryAncestor = adjust(((GenericImpl) oldDependency).supers)[0];
//			Generic implicit = findPrimaryByValue(primaryAncestor, oldDependency.getValue(), oldDependency.getMetaLevel());
//			return implicit != null ? implicit : ((GenericImpl) getEngine().getFactory().newGeneric(oldDependency.getClass())).initializePrimary(oldDependency.getValue(), oldDependency.getMetaLevel(), primaryAncestor, oldDependency.isAutomatic());
//
//		} else
//			return CacheImpl.this.<EngineImpl> getEngine().buildComplex(oldDependency.getClass(), adjust(oldDependency.getImplicit())[0],
//					computeDirectSupers ? getDirectSupers(adjust(((GenericImpl) oldDependency).getPrimariesArray()), adjust(((GenericImpl) oldDependency).components)) : adjust(((GenericImpl) oldDependency).supers),
//					adjust(((GenericImpl) oldDependency).components), oldDependency.isAutomatic());
//	}
//
//	Generic[] adjust(Generic... oldComponents) {
//		Generic[] newComponents = new Generic[oldComponents.length];
//		for (int i = 0; i < newComponents.length; i++) {
//			Generic newComponent = get(oldComponents[i]);
//			assert newComponent == null ? isAlive(oldComponents[i]) : !isAlive(oldComponents[i]) : newComponent + " / " + oldComponents[i].info();
//			newComponents[i] = newComponent == null ? oldComponents[i] : newComponent;
//			assert isAlive(newComponents[i]);
//		}
//		return newComponents;
//	}
//
//	class BindOrganizer extends Organizer {
//
//		@Override
//		NavigableSet<Generic> collectDependencies(Generic old) {
//			return collectDependencies(new Primaries(((GenericImpl) old).supers).toArray(), ((GenericImpl) old).components);
//		}
//
//		NavigableSet<Generic> collectDependencies(Generic[] interfaces, Generic[] components) {
//			NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
//			for (Generic directSuper : getDirectSupers(interfaces, components)) {
//				Iterator<Generic> removeIterator = concernedDependenciesIterator(directSuper, interfaces, components);
//				while (removeIterator.hasNext())
//					orderedDependencies.addAll(orderDependencies(removeIterator.next()));
//			}
//			return orderedDependencies;
//		}
//
//		@Override
//		<T extends Generic> T rebuild(Generic old) {
//			return rebuild(old.getClass(), old.isAutomatic(), old.getImplicit(), ((GenericImpl) old).supers, ((GenericImpl) old).components);
//		}
//
//		<T extends Generic> T rebuild(Class<?> specializeGeneric, boolean automatic, Generic implicit, Generic[] directSupers, Generic[] components) {
//			if (!implicit.isAlive()) {
//				Generic newImplicit = bindPrimaryByValue(((GenericImpl) implicit).supers[0], implicit.getValue(), implicit.getMetaLevel(), implicit.isAutomatic(), implicit.getClass());
//				put(implicit, newImplicit);
//				implicit = newImplicit;
//				directSupers = adjust(directSupers);
//			}
//			return (T) ((GenericImpl) CacheImpl.this.<EngineImpl> getEngine().getFactory().newGeneric(specializeGeneric)).initializeComplex(implicit, directSupers, components, automatic);
//		}
//	}
// }
