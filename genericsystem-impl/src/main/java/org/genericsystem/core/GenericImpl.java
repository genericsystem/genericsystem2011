package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InheritanceDisabled;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.PropertyConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.constraints.SingularInstanceConstraint;
import org.genericsystem.annotation.constraints.UniqueConstraint;
import org.genericsystem.annotation.constraints.VirtualConstraint;
import org.genericsystem.constraints.InstanceClassConstraintImpl;
import org.genericsystem.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.constraints.axed.SingularConstraintImpl;
import org.genericsystem.constraints.simple.NotNullConstraintImpl;
import org.genericsystem.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.constraints.simple.SingularInstanceConstraintImpl;
import org.genericsystem.constraints.simple.UniqueConstraintImpl;
import org.genericsystem.constraints.simple.VirtualConstraintImpl;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;
import org.genericsystem.iterator.ArrayIterator;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.genericsystem.system.CascadeRemoveSystemProperty;
import org.genericsystem.system.ComponentPosValue;
import org.genericsystem.system.MultiDirectionalSystemProperty;
import org.genericsystem.system.NoInheritanceSystemProperty;
import org.genericsystem.system.ReferentialIntegritySystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class GenericImpl implements Generic, Type, Link, Relation, Holder, Attribute, Tree, Node {

	protected static Logger log = LoggerFactory.getLogger(GenericImpl.class);

	private LifeManager lifeManager;

	Generic[] supers;

	Generic[] components;

	int metaLevel;
	Serializable value;

	public Generic[] getSupersArray() {
		return supers.clone();
	}

	public Generic[] getComponentsArray() {
		return components.clone();
	}

	final GenericImpl initializePrimary(Serializable value, int metaLevel, Generic[] directSupers, Generic[] components) {
		return restore(value, metaLevel, null, Long.MAX_VALUE, 0L, Long.MAX_VALUE, directSupers, components);
	}

	final GenericImpl initializeComplex(Generic implicit, Generic[] directSupers, Generic[] components) {
		return restore(implicit.getValue(), implicit.getMetaLevel(), null, Long.MAX_VALUE, 0L, Long.MAX_VALUE, directSupers, components);
	}

	final GenericImpl restore(Serializable value, int metaLevel, Long designTs, long birthTs, long lastReadTs, long deathTs, Generic[] directSupers, Generic[] components) {
		this.value = value;
		this.metaLevel = metaLevel;
		supers = directSupers;
		this.components = components;

		initSelfComponents();
		lifeManager = new LifeManager(designTs == null ? getEngine().pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
		for (Generic g1 : directSupers)
			for (Generic g2 : directSupers)
				if (!g1.equals(g2))
					assert !g1.inheritsFrom(g2) : "" + Arrays.toString(directSupers);
		return this;
	}

	<T extends Generic> T plug() {
		Set<Generic> componentSet = new HashSet<>();
		for (Generic component : components)
			if (componentSet.add(component))
				((GenericImpl) component).lifeManager.engineComposites.add(this);

		Set<Generic> effectiveSupersSet = new HashSet<>();
		for (Generic effectiveSuper : supers)
			if (effectiveSupersSet.add(effectiveSuper))
				((GenericImpl) effectiveSuper).lifeManager.engineDirectInheritings.add(this);
		return (T) this;
	}

	private void initSelfComponents() {
		for (int i = 0; i < components.length; i++)
			if (components[i] == null)
				components[i] = this;
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	@Override
	public int compareTo(Generic generic) {
		return Long.compare(this.getDesignTs(), ((GenericImpl) generic).getDesignTs());
	}

	@Override
	public <T extends Generic> T getImplicit() {
		if (isPrimary())
			return (T) this;
		for (Generic superGeneric : supers)
			if (metaLevel == ((GenericImpl) superGeneric).metaLevel && Objects.hashCode(value) == Objects.hashCode(((GenericImpl) superGeneric).value) && Objects.equals(value, ((GenericImpl) superGeneric).value))
				return ((GenericImpl) superGeneric).getImplicit();
		throw new IllegalStateException(info());
	}

	@Override
	public EngineImpl getEngine() {
		return (EngineImpl) supers[0].getEngine();
	}

	@Override
	public boolean isEngine() {
		return false;
	}

	@Override
	public boolean isInstanceOf(Generic generic) {
		return getMetaLevel() - generic.getMetaLevel() == 1 ? this.inheritsFrom(generic) : false;
	}

	@Override
	public int getMetaLevel() {
		return metaLevel;
	}

	@Override
	public boolean isConcrete() {
		return SystemGeneric.CONCRETE == getMetaLevel();
	}

	@Override
	public boolean isStructural() {
		return SystemGeneric.STRUCTURAL == getMetaLevel();
	}

	@Override
	public boolean isMeta() {
		return SystemGeneric.META == getMetaLevel();
	}

	@Override
	public boolean isType() {
		return components.length == 0;
	}

	@Override
	public boolean isAttribute() {
		return components.length >= 1;
	}

	@Override
	public boolean isReallyAttribute() {
		return components.length == 1;
	}

	@Override
	public boolean isAttributeOf(Generic generic) {
		for (Generic component : components)
			if (generic.inheritsFrom(component))
				return true;
		return false;
	}

	private boolean isAttributeOf(Generic generic, int basePos) {
		if (basePos >= components.length || basePos < 0)
			return false;
		return generic.inheritsFrom(components[basePos]);
	}

	@Override
	public boolean isRelation() {
		return isStructural() && components.length > 1;
	}

	@Override
	public boolean isLink() {
		return isConcrete() && components.length > 1;
	}

	@Override
	public <S extends Serializable> S getValue() {
		return (S) value;
	}

	@Override
	public <T extends Serializable> Snapshot<T> getValues(final Context context, final Attribute attribute) {
		return getHolders(context, attribute).project(new Projector<T, Holder>() {
			@Override
			public T project(Holder holder) {
				return holder.<T> getValue();
			}
		});
	}

	@Override
	public <T extends Serializable> T getValue(Context context, Attribute attribute) {
		Link holder = getHolder(context, attribute);
		return holder != null ? holder.<T> getValue() : null;
	}

	@Override
	public <T extends Holder> T setValue(Cache cache, Attribute attribute, Serializable value) {
		T holder = setHolder(cache, attribute, value);
		assert getValues(cache, attribute).contains(value) : holder.info();
		return holder;
	}

	<T extends Generic> T bindPrimary(Cache cache, Serializable value, int metaLevel) {
		return ((CacheImpl) cache).bindPrimaryByValue(isConcrete() ? this.<GenericImpl> getImplicit().supers[0] : getImplicit(), value, metaLevel);
	}

	@Override
	public <T extends Link> T setLink(Cache cache, Link relation, Serializable value, Generic... targets) {
		return setHolder(cache, relation, value, targets);
	}

	@Override
	public <T extends Link> T setHolder(Cache cache, Holder relation, Serializable value, Generic... targets) {
		int basePos = ((GenericImpl) relation).getFirstComponentPos(this);
		T holder;
		if (((Relation) relation).isSingularConstraintEnabled(cache, basePos))
			holder = getHolder(cache, (Attribute) relation);
		else if (((Type) relation).isPropertyConstraintEnabled(cache))
			holder = getHolder(cache, (Attribute) relation, targets);
		else
			holder = getValuedHolder(cache, (Attribute) relation, value, targets);

		Generic implicit = ((GenericImpl) relation).bindPrimary(cache, value, SystemGeneric.CONCRETE);
		if (holder == null)
			return addLink(cache, implicit, relation, targets);

		if (!this.equals(holder.getComponent(basePos))) {
			if (!isSuperOf(((GenericImpl) holder).getPrimariesArray(), ((GenericImpl) holder).components, new Primaries(implicit, relation).toArray(), Statics.insertIntoArray(this, targets, basePos)))
				internalCancel(cache, holder, basePos);
			return addLink(cache, implicit, relation, targets);
		}
		if (!((GenericImpl) holder).equiv(new Primaries(implicit, relation).toArray(), Statics.insertIntoArray(this, targets, basePos))) {
			holder.remove(cache);
			return setHolder(cache, relation, value, targets);
		}
		return holder;
	}

	public <T extends Link> T getValuedHolder(Context context, Attribute attribute, Serializable value, final Generic... targets) {
		return getValuedHolder(context, attribute, value, getBasePos(attribute), targets);
	}

	public <T extends Link> T getValuedHolder(Context context, Attribute attribute, Serializable value, int basePos, final Generic... targets) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> concreteIterator(context, attribute, basePos, targets), value));
	}

	@Override
	public int getBasePos(Attribute attribute) {
		return ((GenericImpl) attribute).getFirstComponentPos(this);
	}

	@Override
	public <T extends Holder> Snapshot<T> getHolders(final Context context, final Holder attribute, final Generic... targets) {
		return getHolders(context, attribute, getBasePos((Attribute) attribute), targets);
	}

	public <T extends Holder> Snapshot<T> getHolders(final Context context, final Holder attribute, final int basePos, final Generic... targets) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return concreteIterator(context, (Attribute) attribute, basePos, targets);
			}
		};
	}

	@Override
	public <T extends Link> Snapshot<T> getLinks(final Context context, final Relation relation, final Generic... targets) {
		return getLinks(context, relation, getBasePos(relation), targets);
	}

	public <T extends Link> Snapshot<T> getLinks(final Context context, final Relation relation, final int basePos, final Generic... targets) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return linksIterator(context, relation, basePos, targets);
			}
		};
	}

	private <T extends Link> Iterator<T> linksIterator(final Context context, final Relation relation, final int basePos, final Generic... targets) {
		return new AbstractFilterIterator<T>(GenericImpl.this.<T> concreteIterator(context, relation, basePos, targets)) {
			@Override
			public boolean isSelected() {
				return next.isLink();
			}
		};
	}

	@Override
	// TODO KK
	public <T extends Generic> Snapshot<T> getTargets(Context context, Relation relation) {
		return getTargets(context, relation, Statics.TARGET_POSITION);
	}

	@Override
	public <T extends Generic> Snapshot<T> getTargets(Context context, Relation relation, final int targetPos) {
		return getLinks(context, relation).project(new Projector<T, Link>() {
			@Override
			public T project(Link element) {
				return element.getComponent(targetPos);
			}
		});
	}

	private <T extends Holder> Iterator<T> concreteIterator(Context context, Attribute attribute, int basePos, Generic... targets) {
		return Statics.<T> targetsFilter(GenericImpl.this.<T> concreteIterator(context, attribute, basePos, false), attribute, targets);
	}

	@Override
	public <T extends Holder> T getHolder(Context context, Attribute attribute, Generic... targets) {
		return getHolder(context, attribute, getBasePos(attribute), targets);
	}

	private <T extends Holder> T getHolder(Context context, Attribute attribute, int basePos, Generic... targets) {
		return Statics.unambigousFirst(this.<T> concreteIterator(context, attribute, basePos, targets));
	}

	@Override
	public <T extends Link> T getLink(Context context, Relation relation, Generic... targets) {
		return Statics.unambigousFirst(this.<T> linksIterator(context, relation, getBasePos(relation), targets));
	}

	private static <T extends Generic> T update(Cache cache, Generic old, Serializable value) {
		return ((CacheImpl) cache).update(old, value);
	}

	public <T extends Generic> Iterator<T> directInheritingsIterator(Context context) {
		return ((AbstractContext) context).directInheritingsIterator(this);
	}

	@Override
	public <T extends Generic> Snapshot<T> getInheritings(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return directInheritingsIterator(context);
			}
		};
	}

	public <T extends Generic> Iterator<T> compositesIterator(Context context) {
		return ((AbstractContext) context).compositesIterator(this);
	}

	public <T extends Generic> Iterator<T> compositesIterator(Context context, final int pos) {
		return new AbstractFilterIterator<T>(this.<T> compositesIterator(context)) {
			@Override
			public boolean isSelected() {
				return GenericImpl.this.equals(((Holder) next).getComponent(pos));
			}
		};
	}

	@Override
	public <T extends Generic> Snapshot<T> getComposites(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return compositesIterator(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> structuralIterator(Context context) {
		return this.<T> structuralIterator(context, ((AbstractContext) context).getMetaAttribute(), false);
	}

	@Override
	public <T extends Attribute> Snapshot<T> getAttributes(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return structuralIterator(context);
			}
		};
	}

	@Override
	public <T extends Attribute> T getAttribute(final Context context, final Serializable value) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> structuralIterator(context), value));
	}

	private <T extends Relation> Iterator<T> relationsIterator(final Context context) {
		return new AbstractFilterIterator<T>(GenericImpl.this.<T> structuralIterator(context)) {
			@Override
			public boolean isSelected() {
				return next.isRelation();
			}
		};

	}

	@Override
	public <T extends Relation> Snapshot<T> getRelations(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return relationsIterator(context);
			}
		};
	}

	@Override
	public <T extends Relation> T getRelation(final Context context, final Serializable value) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> relationsIterator(context), value));
	}

	public <T extends Generic> T reFind(Cache cache) {
		return ((CacheImpl) cache).reFind(this);
	}

	@Override
	public <T extends Attribute> T getProperty(Context context, Serializable value) {
		return getAttribute(context, value);
	}

	@Override
	public <T extends Attribute> T setAttribute(Cache cache, Serializable value) {
		return setSubAttribute(cache, getEngine(), value);
	}

	@Override
	public <T extends Attribute> T setProperty(Cache cache, Serializable value, Type... targets) {
		return setSubProperty(cache, getEngine(), value, targets);
	}

	public <T extends Attribute> T setSubProperty(Cache cache, Attribute property, Serializable value, Type... targets) {
		return setSubAttribute(cache, property, value, targets).enablePropertyConstraint(cache);
	}

	@Override
	public <T extends Relation> T setRelation(Cache cache, Serializable value, Type... targets) {
		return setSubRelation(cache, getEngine(), value, targets);
	}

	private <T extends Attribute> T setSubRelation(Cache cache, Relation relation, Serializable value, Type... targets) {
		return setSubAttribute(cache, relation, value, targets);
	}

	private <T extends Relation> T setSubAttribute(Cache cache, Attribute attribute, Serializable value, Type... targets) {
		assert !Objects.equals(value, attribute.getValue());
		return addLink(cache, ((GenericImpl) attribute).bindPrimary(cache, value, SystemGeneric.STRUCTURAL), attribute, targets);
	}

	@Override
	public void flag(Cache cache, Attribute attribute) {
		setValue(cache, attribute, Statics.FLAG);
	}

	@Override
	public <T extends Generic> T newAnonymousInstance(Cache cache, Generic... components) {
		return newInstance(cache, getEngine().pickNewAnonymousReference(), components);
	}

	@Override
	public <T extends Generic> T newInstance(Cache cache, Serializable value, Generic... components) {
		return bind(cache, bindPrimary(cache, value, getMetaLevel() + 1), this, components);
	}

	@Override
	public <T extends Type> T newSubType(Cache cache, Serializable value, Generic... components) {
		return bind(cache, bindPrimary(cache, value, SystemGeneric.STRUCTURAL), this, components);
	}

	@Override
	public <T extends Link> T bind(Cache cache, Link relation, Generic... targets) {
		return setLink(cache, relation, Statics.FLAG, targets);
	}

	private Generic[] sortAndCheck(Generic... components) {
		if (getComponentsSize() != components.length)
			throw new IllegalStateException("Illegal components size");
		Map<Generic, Integer> positions = getPositions(components);
		Generic[] orderedComponents = new Generic[components.length];
		for (int i = 0; i < components.length; i++) {
			int pos = positions.get(components[i]);
			if (Statics.NO_POSITION == pos)
				throw new IllegalStateException("Illegal component : " + components[i]);
			orderedComponents[pos] = components[i];
		}
		return orderedComponents;
	}

	private <T extends Link> T addLink(Cache cache, Generic implicit, Generic directSuper, Generic... targets) {
		Generic[] components = Statics.insertFirstIntoArray(this, targets);
		return bind(cache, implicit, directSuper, components);
	}

	// TODO KK
	private <T extends Link> T addLink(Cache cache, Generic implicit, Generic directSuper, int basePos, Generic... targets) {
		return bind(cache, implicit, directSuper, Statics.insertIntoArray(this, targets, basePos));
	}

	private static <T extends Generic> T bind(Cache cache, Generic implicit, Generic directSuper, Generic... components) {
		if (implicit.isConcrete())
			components = ((GenericImpl) directSuper).sortAndCheck(components);
		return ((CacheImpl) cache).bind(implicit, new Generic[] { directSuper }, components);
	}

	private <T extends Generic> Iterator<T> structuralIterator(Context context, Attribute origin, boolean readPhantom) {
		Iterator<T> iterator = ((GenericImpl) origin).safeIsEnabled(context, getNoInheritanceSystemProperty(context)) ? this.<T> noInheritanceStructuralIterator(context, origin) : this.<T> inheritanceStructuralIterator(context, origin);
		return !readPhantom ? Statics.<T> phantomsFilter(iterator) : iterator;
	}

	private <T extends Generic> Iterator<T> concreteIterator(Context context, Attribute origin, int basePos, boolean readPhantom) {
		Iterator<T> iterator = null;
		boolean noInheritance = ((GenericImpl) origin).safeIsEnabled(context, getNoInheritanceSystemProperty(context));
		if (((GenericImpl) origin).safeIsEnabled(context, getMultiDirectionalSystemProperty(context))) {
			Iterator<T>[] iterators = new Iterator[origin.getComponentsSize()];
			for (basePos = 0; basePos < iterators.length; basePos++)
				iterators[basePos] = noInheritance ? this.<T> noInheritanceConcreteIterator(context, origin, basePos) : this.<T> inheritanceConcreteIterator(context, origin, basePos);
			iterator = new ConcateIterator<T>(iterators);
		} else
			iterator = noInheritance ? this.<T> noInheritanceConcreteIterator(context, origin, basePos) : this.<T> inheritanceConcreteIterator(context, origin, basePos);
		return !readPhantom ? Statics.<T> phantomsFilter(iterator) : iterator;
	}

	private Attribute getNoInheritanceSystemProperty(Context context) {
		return ((AbstractContext) context).<Attribute> find(NoInheritanceSystemProperty.class);
	}

	private Attribute getMultiDirectionalSystemProperty(Context context) {
		return ((AbstractContext) context).<Attribute> find(MultiDirectionalSystemProperty.class);
	}

	// @Override
	// public void deduct(final Cache cache) {
	// if (!isPseudoStructural())
	// return;
	// for (int i = 0; i < components.length; i++) {
	// Generic component = components[i];
	// if (component.isStructural())
	// for (Generic inherited : ((Type) component).getInheritings(cache)) {
	// Generic phantom = ((CacheImpl) cache).findPrimaryByValue(((GenericImpl) getImplicit()).supers[0], Statics.PHAMTOM, SystemGeneric.CONCRETE);
	// if (phantom == null || ((CacheImpl) cache).find(Statics.insertFirstIntoArray(phantom, this), Statics.replace(i, components, inherited)) == null)
	// bind(cache, bindPrimary(cache, value, SystemGeneric.CONCRETE), this, Statics.replace(i, components, inherited));
	// }
	// }
	// }

	@Override
	public void deduct(final Cache cache) {
		for (int i = 0; i < components.length; i++) {
			Generic component = components[i];
			if (component.isStructural())
				for (Generic inherited : ((Type) component).getInheritings(cache)) {
					Generic phantom = ((CacheImpl) cache).findPrimaryByValue(isPseudoStructural() ? ((GenericImpl) getImplicit()).supers[0] : getImplicit(), Statics.PHAMTOM, SystemGeneric.CONCRETE);
					if (phantom == null || ((CacheImpl) cache).find(Statics.insertFirstIntoArray(phantom, this), Statics.replace(i, components, inherited)) == null)
						bind(cache, bindPrimary(cache, value, metaLevel), this, Statics.replace(i, components, inherited));
				}
		}
	}

	public boolean isPhantom() {
		return Statics.PHAMTOM.equals(getValue());
	}

	private <T extends Generic> Iterator<T> noInheritanceStructuralIterator(Context context, final Generic origin) {
		return new AbstractFilterIterator<T>(this.<T> compositesIterator(context)) {
			@Override
			public boolean isSelected() {
				return next.isStructural() && next.inheritsFrom(origin);
			}
		};
	}

	private <T extends Generic> Iterator<T> noInheritanceConcreteIterator(Context context, final Generic origin, int pos) {
		return new AbstractFilterIterator<T>(this.<T> compositesIterator(context, pos)) {
			@Override
			public boolean isSelected() {
				return next.isConcrete() && next.inheritsFrom(origin);
			}
		};
	}

	private <T extends Generic> Iterator<T> inheritanceStructuralIterator(final Context context, final Generic origin) {
		return (Iterator<T>) new AbstractSelectableLeafIterator(context, origin) {
			@Override
			protected boolean isSelected(Generic father, Generic candidate) {
				return candidate.getMetaLevel() <= SystemGeneric.STRUCTURAL && candidate.isAttributeOf(GenericImpl.this);
			}

			@Override
			public boolean isSelectable() {
				return next.isStructural() && next.isAttributeOf(GenericImpl.this);
			}
		};
	}

	private <T extends Generic> Iterator<T> inheritanceConcreteIterator(final Context context, final Generic origin, final int pos) {
		return (Iterator<T>) new AbstractSelectableLeafIterator(context, origin) {
			@Override
			protected boolean isSelected(Generic father, Generic candidate) {
				return ((GenericImpl) candidate).isAttributeOf(GenericImpl.this, pos);
			}

			@Override
			public boolean isSelectable() {
				return next.isConcrete() && ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos);
			}
		};
	}

	boolean safeIsEnabled(Context context, Attribute attribute) {
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(context, attribute) {
			@Override
			protected boolean isSelected(Generic father, Generic candidate) {
				return (candidate.getMetaLevel() <= SystemGeneric.CONCRETE) && candidate.isAttributeOf(GenericImpl.this);
			}

			@Override
			public boolean isSelectable() {
				return next.getMetaLevel() == SystemGeneric.CONCRETE;
			}
		};
		return iterator.hasNext() ? Boolean.TRUE.equals(iterator.next().<ComponentPosValue<Boolean>> getValue().getValue()) : false;
	}

	@Override
	// TODO KK
	public boolean inheritsFrom(Generic generic) {
		if (generic == null)
			return false;
		boolean inheritance = ((GenericImpl) generic).new InheritanceCalculator().isSuperOf(this);
		boolean superOf = ((GenericImpl) generic).isSuperOf(this);// ,false
		assert inheritance == superOf : "" + this.info() + generic.info() + " : " + inheritance + " != " + superOf;
		return superOf;
	}

	private Primaries getPrimaries() {
		return new Primaries(this);
	}

	public Generic[] getPrimariesArray() {
		return getPrimaries().toArray();
	}

	// Generic[] getExtendedComponentsArray() {
	// return new ExtendedComponents(components).addSupers(supers).toArray();
	// }

	// static public class ExtendedComponents extends ArrayList<Generic> {
	//
	// private static final long serialVersionUID = 5192296074644405884L;
	//
	// public ExtendedComponents(Generic[] components) {
	// addAll(Arrays.asList(components));
	// }
	//
	// public ExtendedComponents addSupers(Generic... directSupers) {
	// if (directSupers.length == 1 && directSupers[0].isEngine())
	// return this;
	// for (Generic directSuper : directSupers)
	// for (Generic component : ((GenericImpl) directSuper).getExtendedComponentsArray())
	// restrictedAdd(component);
	// return this;
	// }
	//
	// // TODO KK
	// public boolean restrictedAdd(Generic newComponent) {
	// Iterator<Generic> iterator = iterator();
	// while (iterator.hasNext()) {
	// Generic component = iterator.next();
	// if (component != null) {
	// if (((GenericImpl) newComponent).new InheritanceCalculator().isSuperOf(component))
	// return false;
	// } else {
	// continue;
	// }
	// assert component.equals(newComponent) || !((GenericImpl) component).new InheritanceCalculator().isSuperOf(newComponent);
	// }
	// return super.add(newComponent);
	// }
	//
	// @Override
	// public Generic[] toArray() {
	// return super.toArray(new Generic[size()]);
	// }
	// }

	private class InheritanceCalculator extends HashSet<Generic> {
		private static final long serialVersionUID = -894665449193645526L;

		public boolean isSuperOf(Generic subGeneric) {
			if (GenericImpl.this.equals(subGeneric))
				return true;
			for (Generic directSuper : ((GenericImpl) subGeneric).supers)
				if (add(directSuper) && isSuperOf(directSuper))
					return true;
			return false;
		}
	}

	@Override
	public boolean inheritsFromAll(Generic... generics) {
		for (Generic generic : generics)
			if (!inheritsFrom(generic))
				return false;
		return true;
	}

	// TODO KK
	// TODO Pb trees
	// private static boolean isEquals(Generic[] interfaces, Generic[] components, Generic[] subInterfaces, Generic[] subComponents) {
	// return Arrays.equals(interfaces, subInterfaces) && Arrays.equals(components, subComponents);
	// }
	//
	// private static boolean isConcreteInheritance(Generic superGeneric, Generic subGeneric) {
	// return subGeneric.isConcrete() && superGeneric.isConcrete() && subGeneric.getMeta().inheritsFrom(superGeneric.getMeta());
	// }

	// private static boolean isValidConcreteInheritance(Generic[] interfaces, Generic[] components, Generic[] subInterfaces, Generic[] subComponents, int i, boolean onInterfaces) {
	// Generic[] truncate = Statics.truncate(i, onInterfaces ? interfaces : components);
	// Generic[] subTruncate = Statics.truncate(i, onInterfaces ? subInterfaces : subComponents);
	// if (isConcreteInheritance(onInterfaces ? interfaces[i] : components[i], onInterfaces ? subInterfaces[i] : subComponents[i])
	// && !isEquals(onInterfaces ? truncate : interfaces, onInterfaces ? components : truncate, onInterfaces ? subTruncate : subInterfaces, onInterfaces ? subComponents : subTruncate))
	// return isSuperOf(onInterfaces ? truncate : interfaces, onInterfaces ? components : truncate, onInterfaces ? subTruncate : subInterfaces, onInterfaces ? subComponents : subTruncate);
	// return false;
	// }

	// public boolean isSuperOf(Generic generic, boolean override) {
	// assert generic != null;
	// if (equals(generic))
	// return true;
	// if (((GenericImpl) generic).isEngine())
	// return isEngine();
	// if (((GenericImpl) generic).isPrimary())
	// return isSuperOf(((GenericImpl) generic).supers[0], override);
	// return isSuperOf(getPrimariesArray(), components, ((GenericImpl) generic).getPrimariesArray(), ((GenericImpl) generic).components, override);
	// }
	//
	// public static boolean isSuperOf(Generic generic, Generic subGeneric, boolean override) {
	// return isSuperOf(new Primaries(((GenericImpl) generic).getPrimariesArray()).toArray(), ((GenericImpl) generic).components, new Primaries(subGeneric).toArray(), ((GenericImpl) subGeneric).components, override);
	// }

	// public static boolean isSuperOf(Generic[] interfaces, Generic[] components, final Generic[] subInterfaces, Generic[] subComponents, boolean override) {
	// if (interfaces.length == subInterfaces.length && components.length == subComponents.length) {
	// for (int i = 0; i < subInterfaces.length; i++) {
	// if (!((GenericImpl) interfaces[i]).isSuperOf(subInterfaces[i], override))
	// if (!override || !isValidConcreteInheritance(interfaces, components, subInterfaces, subComponents, i, true))
	// return false;
	// }
	// for (int i = 0; i < subComponents.length; i++) {
	// if (components[i] != null && subComponents[i] != null) {
	// if (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)
	// || !Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
	// if (!((GenericImpl) components[i]).isSuperOf(subComponents[i], override))
	// if (!override || !isValidConcreteInheritance(interfaces, components, subInterfaces, subComponents, i, false))
	// return false;
	// }
	// if (components[i] == null) {
	// if (!Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
	// return false;
	// } else if (subComponents[i] == null)
	// if (!components[i].isEngine() && (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)))
	// return false;
	// }
	// return true;
	// }
	// if (subInterfaces.length > 1 && interfaces.length < subInterfaces.length)
	// for (int i = 0; i < subInterfaces.length; i++)
	// if (isSuperOf(interfaces, components, Statics.truncate(i, subInterfaces), subComponents, override))
	// return true;
	// if (components.length < subComponents.length)
	// for (int i = 0; i < subComponents.length; i++)
	// if (isSuperOf(interfaces, components, subInterfaces, Statics.truncate(i, subComponents), override))
	// return true;
	// return false;
	// }

	public boolean isSuperOf(Generic generic) {
		assert generic != null;
		if (equals(generic))
			return true;
		if (((GenericImpl) generic).isEngine())
			return isEngine();
		if (((GenericImpl) generic).isPrimary())
			return isSuperOf(((GenericImpl) generic).supers[0]);
		return isSuperOf(getPrimariesArray(), components, ((GenericImpl) generic).getPrimariesArray(), ((GenericImpl) generic).components);
	}

	public static boolean isSuperOf(Generic[] interfaces, Generic[] components, final Generic[] subInterfaces, Generic[] subComponents) {
		if (interfaces.length == subInterfaces.length && components.length == subComponents.length) {
			for (int i = 0; i < subInterfaces.length; i++) {
				if (!((GenericImpl) interfaces[i]).isSuperOf(subInterfaces[i]))
					return false;
			}
			for (int i = 0; i < subComponents.length; i++) {
				if (components[i] != null && subComponents[i] != null) {
					if (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)
							|| !Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
						if (!((GenericImpl) components[i]).isSuperOf(subComponents[i]))
							return false;
				}
				if (components[i] == null) {
					if (!Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
						return false;
				} else if (subComponents[i] == null)
					if (!components[i].isEngine() && (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)))
						return false;
			}
			return true;
		}
		if (subInterfaces.length > 1 && interfaces.length < subInterfaces.length)
			for (int i = 0; i < subInterfaces.length; i++)
				if (isSuperOf(interfaces, components, Statics.truncate(i, subInterfaces), subComponents))
					return true;
		if (components.length < subComponents.length)
			for (int i = 0; i < subComponents.length; i++)
				if (isSuperOf(interfaces, components, subInterfaces, Statics.truncate(i, subComponents)))
					return true;
		return false;
	}

	// public static boolean isSuperOfStrict(Generic[] interfaces, Generic[] components, final Generic[] subInterfaces, Generic[] subComponents) {
	// if (interfaces.length == subInterfaces.length && components.length == subComponents.length) {
	// for (int i = 0; i < subInterfaces.length; i++) {
	// if (!((GenericImpl) interfaces[i]).isSuperOf(subInterfaces[i]))
	// return false;
	// }
	// for (int i = 0; i < subComponents.length; i++) {
	// if (components[i] != null && subComponents[i] != null) {
	// if (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)
	// || !Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
	// if (!((GenericImpl) components[i]).isSuperOf(subComponents[i]))
	// return false;
	// }
	// if (components[i] == null) {
	// if (!Arrays.equals(subInterfaces, ((GenericImpl) subComponents[i]).getPrimariesArray()) || !Arrays.equals(subComponents, ((GenericImpl) subComponents[i]).components))
	// return false;
	// } else if (subComponents[i] == null)
	// if (!components[i].isEngine() && (!Arrays.equals(interfaces, ((GenericImpl) components[i]).getPrimariesArray()) || !Arrays.equals(components, ((GenericImpl) components[i]).components)))
	// return false;
	// }
	// return true;
	// }
	// if (components.length == subComponents.length && subInterfaces.length > 1 && interfaces.length < subInterfaces.length)
	// for (int i = 0; i < subInterfaces.length; i++)
	// if (isSuperOf(interfaces, components, Statics.truncate(i, subInterfaces), subComponents))
	// return true;
	// return false;
	// }

	@Override
	public void remove(Cache cache) {
		((CacheImpl) cache).remove(this);
	}

	@Override
	public boolean isAlive(Context context) {
		return ((AbstractContext) context).isAlive(this);
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	public long getDesignTs() {
		return lifeManager.getDesignTs();
	}

	public long getBirthTs() {
		return lifeManager.getBirthTs();
	}

	public long getDeathTs() {
		return lifeManager.getDeathTs();
	}

	public long getLastReadTs() {
		return lifeManager.getLastReadTs();
	}

	@Override
	public <T extends Generic> Snapshot<T> getSupers() {
		return new AbstractSnapshot<T>() {

			@Override
			public Iterator<T> iterator() {
				return directSupersIterator();
			}
		};
	}

	private <T extends Generic> Iterator<T> directSupersIterator() {
		return new ArrayIterator<T>((T[]) supers);
	}

	@Override
	public <T extends Generic> Snapshot<T> getComponents() {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return componentsIterator();
			}
		};
	}

	@Override
	public int getComponentsSize() {
		return components.length;
	}

	@Override
	public int getSupersSize() {
		return supers.length;
	}

	private <T extends Generic> Iterator<T> componentsIterator() {
		return new ArrayIterator<T>((T[]) components);
	}

	@Override
	public void log() {
		log.debug(info());
	}

	@Override
	public String info() {
		String s = "\n******************************" + System.identityHashCode(this) + "******************************\n";
		s += "toString()     : " + this + "\n";
		s += "Holder          : " + value + "\n";
		s += "getMeta()      : " + getMeta() + "\n";
		s += "getInstanciationLevel() : " + getMetaLevel() + "\n";
		for (Generic primary : getPrimaries())
			s += "Primary #" + "       : " + primary + " (" + System.identityHashCode(primary) + ")\n";
		for (Generic component : components)
			s += "Component #" + "    : " + component + " (" + System.identityHashCode(component) + ")\n";
		for (Generic superGeneric : supers) {
			s += "Super #" + "        : " + superGeneric + " (" + System.identityHashCode(superGeneric) + ")\n";
		}
		s += "**********************************************************************\n";
		return s;
	}

	@Override
	public String toString() {
		if (isPrimary())
			return value instanceof Class ? ((Class<?>) value).getSimpleName() : value != null ? value.toString() : "null";
		return Arrays.toString(getPrimariesArray()) + "/" + toString(components);
	}

	private String toString(Object[] a) {
		if (a == null)
			return "null";

		int iMax = a.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0;; i++) {
			if (this.equals(a[i]))
				b.append("this");
			else
				b.append(String.valueOf(a[i]));
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}

	public boolean isPrimary() {
		return components.length == 0 && supers.length == 1;
	}

	@Override
	public <T extends Generic> T getMeta() {
		final int instanciationLevel = getMetaLevel() == 0 ? 0 : getMetaLevel() - 1;
		return Statics.levelFilter(new AbstractPreTreeIterator<T>((T) this) {

			private static final long serialVersionUID = 3838947358131801753L;

			@Override
			public Iterator<T> children(T node) {
				return new AbstractFilterIterator<T>(((GenericImpl) node).<T> directSupersIterator()) {
					@Override
					public boolean isSelected() {
						return instanciationLevel <= next.getMetaLevel();
					}
				};
			}
		}, instanciationLevel).next();
	}

	@Override
	public <T extends Generic> T getBaseComponent() {
		return getComponent(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Generic> T getTargetComponent() {
		return getComponent(Statics.TARGET_POSITION);
	}

	@Override
	public <T extends Generic> T getComponent(int componentPos) {
		return components.length <= componentPos || componentPos < 0 ? null : (T) components[componentPos];
	}

	@Override
	public <T extends Generic> Snapshot<T> getInstances(final Context context) {
		// TODO KK change to a prefixed traversal (see getAllInstances)
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return (Iterator<T>) new AbstractSelectableLeafIterator(context, GenericImpl.this) {
					@Override
					protected boolean isSelected(Generic father, Generic candidate) {
						return candidate.isInstanceOf(GenericImpl.this);
					}

					@Override
					public boolean isSelectable() {
						return next.isInstanceOf(GenericImpl.this);
					}
				};
			}
		};
	}

	@Override
	public <T extends Generic> T getInstance(Context context, final Serializable value) {
		return this.<T> getAllInstances(context).filter(new Filter<T>() {
			@Override
			public boolean isSelected(T element) {
				return Objects.equals(element.getValue(), value);
			}
		}).first();
	}

	@Override
	public <T extends Generic> Snapshot<T> getAllInstances(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return allInstancesIterator(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> allInstancesIterator(Context context) {
		return Statics.levelFilter(this.<T> allInheritingsAboveIterator(context, getMetaLevel() + 1), getMetaLevel() + 1);
	}

	private <T extends Generic> Iterator<T> allInheritingsAboveIterator(final Context context, final int metaLevel) {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {

			private static final long serialVersionUID = 7164424160379931253L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return new AbstractFilterIterator<Generic>(((GenericImpl) node).directInheritingsIterator(context)) {
					@Override
					public boolean isSelected() {
						return next.getMetaLevel() <= metaLevel;
					}
				};
			}
		};
	}

	@Override
	public <T extends Generic> T getSubType(Context context, final Serializable value) {
		return this.<T> getSubTypes(context).filter(new Filter<T>() {
			@Override
			public boolean isSelected(T element) {
				return Objects.equals(element.getValue(), value);
			}
		}).first();
	}

	@Override
	public <T extends Generic> Snapshot<T> getDirectSubTypes(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return directSubTypesIterator(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> directSubTypesIterator(Context context) {
		return Statics.levelFilter(this.<T> directInheritingsIterator(context), getMetaLevel());
	}

	@Override
	public <T extends Generic> Snapshot<T> getSubTypes(final Context context) {
		return new AbstractSnapshot<T>() {

			@Override
			public Iterator<T> iterator() {
				return allSubTypesIteratorWithoutRoot(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> allSubTypesIteratorWithoutRoot(Context context) {
		return Statics.levelFilter(this.<T> allInheritingsIteratorWithoutRoot(context), getMetaLevel());
	}

	public <T extends Generic> Snapshot<T> getAllInheritings(final Context context) {
		return new AbstractSnapshot<T>() {

			@Override
			public Iterator<T> iterator() {
				return allInheritingsIterator(context);
			}
		};
	}

	public <T extends Generic> Snapshot<T> getAllInheritingsWithoutRoot(final Context context) {
		return new AbstractSnapshot<T>() {

			@Override
			public Iterator<T> iterator() {
				return allInheritingsIteratorWithoutRoot(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> allInheritingsIterator(final Context context) {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {

			private static final long serialVersionUID = 4540682035671625893L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return (((GenericImpl) node).directInheritingsIterator(context));
			}
		};
	}

	private <T extends Generic> Iterator<T> allInheritingsIteratorWithoutRoot(final Context context) {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {

			{
				next();
			}

			private static final long serialVersionUID = 4540682035671625893L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return (((GenericImpl) node).directInheritingsIterator(context));
			}
		};
	}

	void mountConstraints(Cache cache, Class<?> clazz) {
		if (clazz.getAnnotation(InheritanceDisabled.class) != null)
			disableInheritance(cache);

		if (clazz.getAnnotation(VirtualConstraint.class) != null)
			enableVirtualConstraint(cache);

		if (clazz.getAnnotation(UniqueConstraint.class) != null)
			enableUniqueConstraint(cache);

		InstanceClassConstraint instanceClass = clazz.getAnnotation(InstanceClassConstraint.class);
		if (instanceClass != null)
			setConstraintClass(cache, instanceClass.value());

		if (clazz.getAnnotation(NotNullConstraint.class) != null)
			enableNotNullConstraint(cache);

		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			enablePropertyConstraint(cache);

		if (clazz.getAnnotation(SingularInstanceConstraint.class) != null)
			enableSingularInstanceConstraint(cache);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				enableSingularConstraint(cache, axe);
	}

	Map<Generic, Integer> getPositions(Generic... components) {
		Map<Generic, Integer> positions = new LinkedHashMap<>();
		for (Generic component : components)
			positions.put(component, getComponentPos(component, positions.values()));
		return positions;
	}

	public int getComponentPos(Generic generic, Collection<Integer> forbiddenComponentPos) {
		for (int i = 0; i < getComponentsSize(); i++)
			if (!forbiddenComponentPos.contains(i) && (generic == null ? this.equals(getComponent(i)) : generic.inheritsFrom(getComponent(i))))
				return i;
		return Statics.NO_POSITION;
	}

	public int getFirstComponentPos(Generic component) {
		for (int i = 0; i < getComponentsSize(); i++)
			if (component.inheritsFrom(getComponent(i)))
				return i;
		return Statics.NO_POSITION;
	}

	/*********************************************/
	/**************** PHANTOM ********************/
	/*********************************************/

	@Override
	public void cancel(Cache cache, Holder attribute) {
		cancel(cache, attribute, ((GenericImpl) attribute).getFirstComponentPos(this));
	}

	@Override
	public void cancel(Cache cache, Holder attribute, int basePos) {
		if (equals(attribute.getComponent(basePos)))
			throw new IllegalStateException("Only inherited attributes can be cancelled");
		// TODO ???
		// assert Statics.replace(basePos, ((GenericImpl) attribute).components, this).length != 0;

		if (isSpecializedAttribute(cache, attribute, basePos))
			throw new IllegalStateException("Attribute " + attribute + " is already override");

		((GenericImpl) attribute).deduct(cache);
		internalCancel(cache, attribute, basePos);
	}

	private boolean isSpecializedAttribute(Context context, Holder attribute, int basePos) {
		if (attribute.isStructural() && !getAttribute(context, attribute.getValue()).equals(attribute))
			return true;

		Holder holder = getHolder(context, (Attribute) attribute, basePos);
		if (holder != null && ((GenericImpl) holder).isPseudoStructural())
			return !holder.equals(attribute);

		return false;
	}

	private void internalCancel(Cache cache, Holder attribute, int basePos) {
		addLink(cache, ((GenericImpl) attribute).bindPrimary(cache, Statics.PHAMTOM, attribute.getMetaLevel()), attribute.isStructural() ? getAttribute(cache, attribute.getValue()) : attribute, basePos,
				Statics.truncate(basePos, ((GenericImpl) attribute).components));
	}

	@Override
	public void restore(Cache cache, Holder attribute) {
		restore(cache, attribute, getBasePos((Attribute) attribute));
	}

	@Override
	public void restore(final Cache cache, final Holder attribute, final int basePos) {
		Iterator<Holder> holders = attribute.isConcrete() ? GenericImpl.this.<Holder> concreteIterator(cache, (Attribute) attribute, basePos, true) : GenericImpl.this.<Holder> structuralIterator(cache, (Attribute) attribute, true);
		while (holders.hasNext()) {
			Holder nodeValue = holders.next();
			if (equals(nodeValue.getComponent(basePos)) && ((GenericImpl) nodeValue).isPhantom() && Objects.equals(((GenericImpl) nodeValue).supers[0].getValue(), attribute.getValue()))
				nodeValue.remove(cache);
		}
	}

	/*********************************************/
	/******************* TREE ********************/
	/*********************************************/

	@Override
	public <T extends Node> T newRoot(Cache cache, Serializable value) {
		return newRoot(cache, value, 1);
	}

	// TODO KK
	@Override
	public <T extends Node> T newRoot(Cache cache, Serializable value, int dim) {
		return bind(cache, bindPrimary(cache, value, SystemGeneric.CONCRETE), this, new Generic[dim]);
	}

	@Override
	public <T extends Node> T addNode(Cache cache, Serializable value, Generic... targets) {
		return addLink(cache, this.<GenericImpl> getMeta().bindPrimary(cache, value, SystemGeneric.CONCRETE), getMeta(), targets);
	}

	@Override
	public <T extends Node> T addSubNode(Cache cache, Serializable value, Generic... targets) {
		return addLink(cache, bindPrimary(cache, value, SystemGeneric.CONCRETE), this, targets);
	}

	@Override
	public <T extends Node> Snapshot<T> getChildren(Context context) {
		return this.<T> getHolders(context, this.<Tree> getMeta()).filter(new Filter<T>() {
			@Override
			public boolean isSelected(T node) {
				return !GenericImpl.this.equals(node);
			}
		});
	}

	@Override
	public boolean isTree() {
		for (int i = 0; i < components.length; i++)
			if (equals(components[i]))
				return true;
		return false;
	}

	@Override
	public void traverse(Visitor visitor) {
		visitor.traverse(this);
	}

	/*********************************************/
	/************** SYSTEM PROPERTY **************/
	/*********************************************/

	private boolean isSystemPropertyDefaultEnabled(Class<?> systemPropertyClass) {
		return systemPropertyClass.getAnnotation(SystemGeneric.class).defaultBehavior();
	}

	@Override
	public <T extends Generic> T enableSystemProperty(Cache cache, Class<?> systemPropertyClass) {
		setSystemPropertyValue(cache, cache.<Attribute> find(systemPropertyClass), Boolean.TRUE);
		return (T) this;
	}

	@Override
	public <T extends Generic> T disableSystemProperty(Cache cache, Class<?> systemPropertyClass) {
		setSystemPropertyValue(cache, cache.<Attribute> find(systemPropertyClass), Boolean.FALSE);
		return (T) this;
	}

	@Override
	public <T extends Generic> T enableSystemProperty(Cache cache, Class<?> systemPropertyClass, int basePos) {
		return setSystemPropertyValue(cache, cache.<Attribute> find(systemPropertyClass), basePos, Boolean.TRUE);
	}

	@Override
	public <T extends Generic> T disableSystemProperty(Cache cache, Class<?> systemPropertyClass, int basePos) {
		return setSystemPropertyValue(cache, cache.<Attribute> find(systemPropertyClass), basePos, Boolean.FALSE);
	}

	<T extends Generic> T setSystemPropertyValue(Cache cache, Attribute systemProperty, Serializable value) {
		return setSystemPropertyValue(cache, systemProperty, getBasePos(systemProperty), value);
	}

	// TODO super KK
	boolean isSystemPropertyEnabled(Context context, Attribute systemProperty, int basePos) {
		for (Holder valueHolder : getHolders(context, systemProperty))
			if (Objects.equals(valueHolder.<ComponentPosValue<Serializable>> getValue().getComponentPos(), basePos))
				return !Boolean.FALSE.equals(valueHolder.<ComponentPosValue<Serializable>> getValue().getValue());
		return isSystemPropertyDefaultEnabled(systemProperty.<Class<?>> getValue());
	}

	<T extends Serializable> T getSystemPropertyValue(Context context, Attribute systemProperty) {
		return getSystemPropertyValue(context, systemProperty, getBasePos(systemProperty));
	}

	<T extends Serializable> T getSystemPropertyValue(Context context, Attribute systemProperty, int basePos) {
		for (Holder valueHolder : getHolders(context, systemProperty))
			if (Objects.equals(valueHolder.<ComponentPosValue<Serializable>> getValue().getComponentPos(), basePos))
				return valueHolder.<ComponentPosValue<T>> getValue().getValue();
		return null;
	}

	<T extends Generic> T setSystemPropertyValue(Cache cache, Attribute systemProperty, int basePos, Serializable value) {
		// TODO basePos +1 ???
		// if (basePos + 1 > getComponentsSize())
		if (basePos > getComponentsSize())
			throw new IllegalStateException("The component position (" + basePos + ") exceeds the components size " + this.info());
		for (Holder holder : getHolders(cache, systemProperty))
			if (Objects.equals(holder.<ComponentPosValue<Boolean>> getValue().getComponentPos(), basePos)) {

				if (!this.equals(holder.getComponent(basePos)))
					addLink(cache, ((GenericImpl) holder).bindPrimary(cache, new ComponentPosValue<Serializable>(basePos, value), SystemGeneric.CONCRETE), holder);
				else {
					// holder.remove(cache);
					// Generic implicit = ((GenericImpl) attribute).bindPrimary(cache, new ComponentPosValue<Serializable>(basePos, enabled), SystemGeneric.CONCRETE);
					// addLink(cache, implicit, attribute);
					update(cache, holder, new ComponentPosValue<Serializable>(basePos, value));
				}
				return (T) this;
			}
		Generic implicit = ((GenericImpl) systemProperty).bindPrimary(cache, new ComponentPosValue<Serializable>(basePos, value), SystemGeneric.CONCRETE);
		addLink(cache, implicit, systemProperty);
		return (T) this;
	}

	@Override
	public boolean isSystemPropertyEnabled(Context context, Class<?> systemPropertyClass) {
		return isSystemPropertyEnabled(context, ((AbstractContext) context).<Attribute> find(systemPropertyClass));
	}

	@Override
	public boolean isSystemPropertyEnabled(Context context, Class<?> systemPropertyClass, int basePos) {
		return isSystemPropertyEnabled(context, ((AbstractContext) context).<Attribute> find(systemPropertyClass), basePos);
	}

	boolean isSystemPropertyEnabled(Context context, Attribute systemProperty) {
		return isSystemPropertyEnabled(context, systemProperty, getBasePos(systemProperty));
	}

	@Override
	public <T extends Attribute> T enableMultiDirectional(Cache cache) {
		return enableSystemProperty(cache, MultiDirectionalSystemProperty.class);
	}

	@Override
	public <T extends Attribute> T disableMultiDirectional(Cache cache) {
		return disableSystemProperty(cache, MultiDirectionalSystemProperty.class);
	}

	@Override
	public boolean isMultiDirectional(Context context) {
		return isSystemPropertyEnabled(context, MultiDirectionalSystemProperty.class);
	}

	@Override
	public <T extends Relation> T enableCascadeRemove(Cache cache, int basePos) {
		return enableSystemProperty(cache, CascadeRemoveSystemProperty.class, basePos);
	}

	@Override
	public <T extends Relation> T disableCascadeRemove(Cache cache, int basePos) {
		return disableSystemProperty(cache, CascadeRemoveSystemProperty.class, basePos);
	}

	@Override
	public boolean isCascadeRemove(Context context, int basePos) {
		return isSystemPropertyEnabled(context, CascadeRemoveSystemProperty.class, basePos);
	}

	@Override
	public <T extends Generic> T enableReferentialIntegrity(Cache cache, int componentPos) {
		return enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, componentPos);
	}

	@Override
	public <T extends Generic> T disableReferentialIntegrity(Cache cache, int componentPos) {
		return disableSystemProperty(cache, ReferentialIntegritySystemProperty.class, componentPos);
	}

	@Override
	public boolean isReferentialIntegrity(Context context, int basePos) {
		return isSystemPropertyEnabled(context, ReferentialIntegritySystemProperty.class, basePos);
	}

	@Override
	public <T extends Type> T enableSingularConstraint(Cache cache) {
		return enableSystemProperty(cache, SingularConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableSingularConstraint(Cache cache) {
		return disableSystemProperty(cache, SingularConstraintImpl.class);
	}

	@Override
	public boolean isSingularConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, SingularConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableSingularConstraint(Cache cache, int basePos) {
		return enableSystemProperty(cache, SingularConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Type> T disableSingularConstraint(Cache cache, int basePos) {
		return disableSystemProperty(cache, SingularConstraintImpl.class, basePos);
	}

	@Override
	public boolean isSingularConstraintEnabled(Context context, int basePos) {
		return isSystemPropertyEnabled(context, SingularConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Type> T enablePropertyConstraint(Cache cache) {
		return enableSystemProperty(cache, PropertyConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disablePropertyConstraint(Cache cache) {
		return disableSystemProperty(cache, PropertyConstraintImpl.class);
	}

	@Override
	public boolean isPropertyConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, PropertyConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableRequiredConstraint(Cache cache) {
		return enableSystemProperty(cache, RequiredConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableRequiredConstraint(Cache cache) {
		return disableSystemProperty(cache, RequiredConstraintImpl.class);
	}

	@Override
	public boolean isRequiredConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, RequiredConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableRequiredConstraint(Cache cache, int basePos) {
		return enableSystemProperty(cache, RequiredConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Type> T disableRequiredConstraint(Cache cache, int basePos) {
		return disableSystemProperty(cache, RequiredConstraintImpl.class, basePos);
	}

	@Override
	public boolean isRequiredConstraintEnabled(Context context, int basePos) {
		return isSystemPropertyEnabled(context, RequiredConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Type> T enableNotNullConstraint(Cache cache) {
		return enableSystemProperty(cache, NotNullConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableNotNullConstraint(Cache cache) {
		return disableSystemProperty(cache, NotNullConstraintImpl.class);
	}

	@Override
	public boolean isNotNullConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, NotNullConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableUniqueConstraint(Cache cache) {
		return enableSystemProperty(cache, UniqueConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableUniqueConstraint(Cache cache) {
		return disableSystemProperty(cache, UniqueConstraintImpl.class);
	}

	@Override
	public boolean isUniqueConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, UniqueConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableVirtualConstraint(Cache cache) {
		return enableSystemProperty(cache, VirtualConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableVirtualConstraint(Cache cache) {
		return disableSystemProperty(cache, VirtualConstraintImpl.class);
	}

	@Override
	public boolean isVirtualConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, VirtualConstraintImpl.class);
	}

	@Override
	public Class<?> getConstraintClass(Cache cache) {
		ComponentPosValue<Class<?>> value = this.<ComponentPosValue<Class<?>>> getValue(cache, cache.<Attribute> find(InstanceClassConstraintImpl.class));
		return value == null ? Object.class : value.getValue();
	}

	@Override
	public <T extends Type> T setConstraintClass(Cache cache, Class<?> constraintClass) {
		return setSystemPropertyValue(cache, cache.<Attribute> find(InstanceClassConstraintImpl.class), constraintClass);
	}

	@Override
	public <T extends Type> T enableSingularInstanceConstraint(Cache cache) {
		return enableSystemProperty(cache, SingularInstanceConstraintImpl.class);
	}

	@Override
	public <T extends Type> T disableSingularInstanceConstraint(Cache cache) {
		return disableSystemProperty(cache, SingularInstanceConstraintImpl.class);
	}

	@Override
	public boolean isSingularInstanceConstraintEnabled(Context context) {
		return isSystemPropertyEnabled(context, SingularInstanceConstraintImpl.class);
	}

	@Override
	public <T extends Type> T enableInheritance(Cache cache) {
		return disableSystemProperty(cache, NoInheritanceSystemProperty.class);
	}

	@Override
	public <T extends Type> T disableInheritance(Cache cache) {
		return enableSystemProperty(cache, NoInheritanceSystemProperty.class);
	}

	@Override
	public boolean isInheritanceEnabled(Context context) {
		return !isSystemPropertyEnabled(context, NoInheritanceSystemProperty.class);
	}

	public Generic[] transform(Generic[] components) {
		Generic[] result = components.clone();
		for (int i = 0; i < result.length; i++)
			if (result[i] == null)
				result[i] = this;
		return result;
	}

	boolean equiv(Generic[] interfaces, Generic[] components) {
		return Arrays.equals(getPrimariesArray(), interfaces) && Arrays.equals(transform(components), this.components);
	}

	public <T extends Generic> T reBind(Cache cache) {
		return ((CacheImpl) cache).reBind(this);
	}

	boolean isPseudoStructural() {
		if (!isConcrete())
			return false;
		for (Generic component : components)
			if (component.isStructural())
				return true;
		return false;
	}

}
