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
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.PropertyConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.constraints.SingularInstanceConstraint;
import org.genericsystem.annotation.constraints.UniqueConstraint;
import org.genericsystem.annotation.constraints.VirtualConstraint;
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
import org.genericsystem.iterator.CartesianIterator;
import org.genericsystem.iterator.SingletonIterator;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.MultiDirectionalSystemProperty;
import org.genericsystem.systemproperties.NoInheritanceSystemProperty;
import org.genericsystem.systemproperties.ReferentialIntegritySystemProperty;
import org.genericsystem.systemproperties.constraints.InstanceClassConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.SingularInstanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UniqueConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.VirtualConstraintImpl;
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

	Serializable value;

	boolean automatic;

	public Generic[] getSupersArray() {
		return supers.clone();
	}

	public Generic[] getComponentsArray() {
		return components.clone();
	}

	// public boolean isFlushable(Cache cache) {
	// return ((CacheImpl) cache).isFlushable(this);
	// }

	@Override
	public boolean isAutomatic() {
		return automatic;
	}

	final GenericImpl initializePrimary(Serializable value, int metaLevel, Generic[] directSupers, Generic[] components, boolean automatic) {
		return restore(value, metaLevel, null, Long.MAX_VALUE, 0L, Long.MAX_VALUE, directSupers, components, automatic);
	}

	// TODO clean
	final GenericImpl initializeComplex(Generic implicit, Generic[] directSupers, Generic[] components, boolean automatic) {
		assert ((GenericImpl) implicit).isPrimary() : "implicit isn't primary";
		reorderImplicit(implicit, directSupers);
		return restore(implicit.getValue(), implicit.getMetaLevel(), null, Long.MAX_VALUE, 0L, Long.MAX_VALUE, directSupers, components, automatic);
	}

	private void reorderImplicit(Generic implicit, Generic[] supers) {
		for (int index = 0; index < supers.length; index++)
			if (implicit.equals(supers[index].getImplicit()))
				if (index != 0) {
					switchFirst(index, supers);
					return;
				}
	}

	private static void switchFirst(int index, Generic[] supers) {
		Generic tmp = supers[index];
		supers[index] = supers[0];
		supers[0] = tmp;
	}

	// private boolean contains(Generic search) {
	// if (equals(search))
	// return true;
	// if (isEngine())
	// return false;
	// for (Generic superGeneric : supers)
	// if (((GenericImpl) superGeneric).contains(search))
	// return true;
	// return false;
	// }

	final GenericImpl restore(Serializable value, int metaLevel, Long designTs, long birthTs, long lastReadTs, long deathTs, Generic[] directSupers, Generic[] components, boolean automatic) {
		this.value = value;
		supers = directSupers;
		this.components = components;
		this.automatic = automatic;

		initSelfComponents();
		lifeManager = new LifeManager(designTs == null ? getEngine().pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
		for (Generic g1 : directSupers)
			for (Generic g2 : directSupers)
				if (!g1.equals(g2))
					assert !g1.inheritsFrom(g2) : "" + Arrays.toString(directSupers);

		// TODO KK
		assert getMetaLevel() == metaLevel : this + " => getMetaLevel() : " + getMetaLevel() + " / metaLevel : " + metaLevel;
		if (!isPrimary())
			assert Objects.equals(directSupers[0].getValue(), value);
		// zassert /* components.length != 0 || */value != null;
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
		long birthTs = getBirthTs();
		long compareBirthTs = ((GenericImpl) generic).getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getDesignTs(), ((GenericImpl) generic).getDesignTs()) : Long.compare(birthTs, compareBirthTs);
	}

	@Override
	public <T extends Generic> T getImplicit() {
		return isPrimary() ? (T) this : supers[0].<T> getImplicit();
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

	// TODO clean
	// @Override
	// public int getMetaLevel() {
	// return metaLevel;
	// }

	@Override
	public int getMetaLevel() {
		return isPrimary() ? supers[0].getMetaLevel() + 1 : getImplicit().getMetaLevel();
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

	@Override
	public boolean isAttributeOf(Generic generic, int basePos) {
		if (basePos < 0 || basePos >= components.length)
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
		return (S) value;// (S) getEngine().getValue(this);
	}

	@Override
	public <T extends Serializable> Snapshot<T> getValues(final Context context, final Holder attribute) {
		return getHolders(context, attribute).project(new Projector<T, Holder>() {
			@Override
			public T project(Holder holder) {
				return holder.<T> getValue();
			}
		});
	}

	@Override
	public <T extends Serializable> T getValue(Context context, Holder attribute) {
		Link holder = getHolder(context, attribute);
		return holder != null ? holder.<T> getValue() : null;
	}

	@Override
	public <T extends Holder> T setValue(Cache cache, Holder attribute, Serializable value) {
		T holder = setHolder(cache, attribute, value);
		assert value == null || getValues(cache, attribute).contains(value) : holder;
		return holder;
	}

	<T extends Generic> T bindPrimary(Cache cache, Serializable value, int metaLevel, boolean automatic) {
		return ((CacheImpl) cache).bindPrimaryByValue(isConcrete() ? this.<GenericImpl> getImplicit().supers[0] : getImplicit(), value, metaLevel, automatic);
	}

	@Override
	public <T extends Link> T setLink(Cache cache, Link relation, Serializable value, Generic... targets) {
		return setHolder(cache, relation, value, targets);
	}

	@Override
	public <T extends Link> T setHolder(Cache cache, Holder attribute, Serializable value, Generic... targets) {
		return setHolder(cache, attribute, value, getBasePos(attribute, targets), targets);
	}

	@Override
	public <T extends Link> T setHolder(Cache cache, Holder attribute, Serializable value, int basePos, Generic... targets) {
		T holder;
		if (((Relation) attribute).isSingularConstraintEnabled(cache, basePos))
			holder = getHolder(cache, (Attribute) attribute, basePos);
		else if (((Type) attribute).isPropertyConstraintEnabled(cache))
			holder = getHolder(cache, (Attribute) attribute, basePos, targets);
		else if (value == null)
			holder = getHolder(cache, (Attribute) attribute, basePos, targets);
		else
			holder = getHolderByValue(cache, (Attribute) attribute, value, basePos, targets);

		Generic implicit = ((GenericImpl) attribute).bindPrimary(cache, value, SystemGeneric.CONCRETE, true);
		if (holder == null) {
			if (value == null && attribute.isStructural())
				return null;
			return this.<T> bind(cache, implicit, attribute, basePos, targets);
		}
		if (!this.equals(holder.getComponent(basePos))) {
			if (!(((GenericImpl) holder).equiv(new Primaries(implicit, attribute).toArray(), Statics.insertIntoArray(holder.getComponent(basePos), targets, basePos)))) {
				Generic phantomImplicit = ((GenericImpl) attribute).bindPrimary(cache, null, SystemGeneric.CONCRETE, true);
				T phantom = bind(cache, phantomImplicit, holder, basePos, Statics.truncate(basePos, ((GenericImpl) holder).components));
				if (value == null)
					return phantom;
			}
			return this.<T> bind(cache, implicit, attribute, basePos, targets);
		}
		if (((GenericImpl) holder).equiv(new Primaries(implicit, attribute).toArray(), Statics.insertIntoArray(this, targets, basePos)))
			return holder;
		holder.remove(cache);
		return this.<T> setHolder(cache, attribute, value, basePos, targets);
	}

	@Override
	public void clear(Cache cache, Holder attribute, int basePos, Generic... targets) {
		for (Holder holder : getHolders(cache, (Attribute) attribute, targets)) {
			if (this.equals(holder.getComponent(basePos)))
				holder.remove(cache);
			else {
				Generic phantomImplicit = ((GenericImpl) attribute).bindPrimary(cache, null, SystemGeneric.CONCRETE, true);
				bind(cache, phantomImplicit, holder, basePos, Statics.truncate(basePos, ((GenericImpl) holder).components));
			}
		}
	}

	@Override
	public void clear(Cache cache, Holder attribute, Generic... targets) {
		clear(cache, attribute, getBasePos(attribute, targets), targets);
	}

	public <T extends Link> T getHolderByValue(Context context, Holder attribute, Serializable value, final Generic... targets) {
		return getHolderByValue(context, attribute, value, getBasePos(attribute, targets), targets);
	}

	public <T extends Link> T getHolderByValue(Context context, Holder attribute, Serializable value, int basePos, final Generic... targets) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> concreteIterator(context, attribute, basePos, value == null, targets), value));
	}

	@Override
	public int getBasePos(Holder attribute, Generic[] targets) {
		return ((GenericImpl) attribute).getPositions(Statics.insertFirst(this, targets)).get(0);
	}

	@Override
	public <T extends Holder> Snapshot<T> getHolders(final Context context, final Holder attribute, final Generic... targets) {
		return getHolders(context, attribute, getBasePos(attribute, targets), targets);
	}

	@Override
	public <T extends Holder> Snapshot<T> getHolders(final Context context, final Holder attribute, final int basePos, final Generic... targets) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return concreteIterator(context, (Attribute) attribute, basePos, false, targets);
			}
		};
	}

	@Override
	public <T extends Link> Snapshot<T> getLinks(final Context context, final Relation relation, final Generic... targets) {
		return getLinks(context, relation, getBasePos(relation, targets), targets);
	}

	@Override
	public <T extends Link> Snapshot<T> getLinks(final Context context, final Relation relation, final int basePos, final Generic... targets) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return linksIterator(context, relation, basePos, targets);
			}
		};
	}

	private <T extends Link> Iterator<T> linksIterator(final Context context, final Link relation, final int basePos, final Generic... targets) {
		return new AbstractFilterIterator<T>(GenericImpl.this.<T> concreteIterator(context, relation, basePos, false, targets)) {
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

	<T extends Generic> Iterator<T> targetsFilter(Iterator<T> iterator, Holder attribute, final Generic... targets) {
		final Map<Integer, Integer> positions = ((GenericImpl) attribute).getPositions(Statics.insertFirst(this, targets));
		return new AbstractFilterIterator<T>(iterator) {
			@Override
			public boolean isSelected() {
				for (int i = 0; i < targets.length; i++)
					if (!targets[i].equals(((Holder) next).getComponent(positions.get(i + 1))))
						return false;
				return true;
			}
		};
	}

	public <T extends Holder> Iterator<T> concreteIterator(Context context, Holder attribute, int basePos, boolean readPhantoms, Generic... targets) {
		return this.<T> targetsFilter(GenericImpl.this.<T> concreteIterator(context, attribute, basePos, readPhantoms), attribute, targets);
	}

	@Override
	public <T extends Holder> T getHolder(Context context, Holder attribute, Generic... targets) {
		return getHolder(context, attribute, getBasePos(attribute, targets), targets);
	}

	@Override
	public <T extends Holder> T getHolder(Context context, Holder attribute, int basePos, Generic... targets) {
		return Statics.unambigousFirst(this.<T> concreteIterator(context, attribute, basePos, false, targets));
	}

	@Override
	public <T extends Link> T getLink(Context context, Link relation, int basePos, Generic... targets) {
		return Statics.unambigousFirst(this.<T> linksIterator(context, relation, basePos, targets));
	}

	@Override
	public <T extends Link> T getLink(Context context, Link relation, Generic... targets) {
		return Statics.unambigousFirst(this.<T> linksIterator(context, relation, getBasePos(relation, targets), targets));
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

	public <T extends Generic> Iterator<T> attributesIterator(Context context, boolean readPhantoms) {
		return this.<T> attributesIterator(context, ((AbstractContext) context).getMetaAttribute(), readPhantoms);
	}

	@Override
	public <T extends Attribute> Snapshot<T> getAttributes(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return attributesIterator(context, false);
			}
		};
	}

	@Override
	public <T extends Attribute> Snapshot<T> getAttributes(final Context context, final Attribute attribute) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return attributesIterator(context, attribute, false);
			}
		};
	}

	@Override
	public <T extends Attribute> T getAttribute(final Context context, Attribute attribute, final Serializable value) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> attributesIterator(context, attribute, value == null), value));
	}

	@Override
	public <T extends Attribute> T getAttribute(final Context context, final Serializable value) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> attributesIterator(context, value == null), value));
	}

	private <T extends Relation> Iterator<T> relationsIterator(final Context context, boolean readPhantom) {
		return new AbstractFilterIterator<T>(GenericImpl.this.<T> attributesIterator(context, readPhantom)) {
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
				return relationsIterator(context, false);
			}
		};
	}

	@Override
	public <T extends Relation> T getRelation(final Context context, final Serializable value) {
		return Statics.unambigousFirst(Statics.valueFilter(this.<T> relationsIterator(context, value == null), value));
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

	public <T extends Relation> T setSubAttribute(Cache cache, Attribute attribute, Serializable value, Type... targets) {
		return bind(cache, getEngine().bindPrimary(cache, value, SystemGeneric.STRUCTURAL, true), attribute, getBasePos(attribute, targets), targets);
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
		return ((CacheImpl) cache).bind(bindPrimary(cache, value, getMetaLevel() + 1, false), false, this, components);
	}

	@Override
	public <T extends Type> T newSubType(Cache cache, Serializable value, Generic... components) {
		Generic implicit = getEngine().bindPrimary(cache, value, SystemGeneric.STRUCTURAL, true);
		return ((CacheImpl) cache).bind(implicit, false, this, components);
	}

	@Override
	public <T extends Link> T bind(Cache cache, Link relation, Generic... targets) {
		return setLink(cache, relation, Statics.FLAG, targets);
	}

	Generic[] sortAndCheck(Generic... components) {
		if (getComponentsSize() != components.length)
			throw new IllegalStateException("Illegal components size");
		Map<Integer, Integer> positions = getPositions(components);
		Generic[] orderedComponents = new Generic[components.length];
		for (int i = 0; i < components.length; i++) {
			int pos = positions.get(i);
			if (pos < 0 || pos >= components.length)
				throw new IllegalStateException("Unable to find a valid positiojn for : " + components[i]);
			orderedComponents[pos] = components[i];
		}
		return orderedComponents;
	}

	private <T extends Link> T bind(Cache cache, Generic implicit, Holder directSuper, int basePos, Generic... targets) {
		return ((CacheImpl) cache).bind(implicit, false, directSuper, Statics.insertIntoArray(this, targets, basePos));
	}

	public <T extends Generic> Iterator<T> attributesIterator(Context context, Attribute origin, boolean readPhantom) {
		Iterator<T> iterator = ((GenericImpl) origin).safeIsEnabled(context, getNoInheritanceSystemProperty(context)) ? this.<T> noInheritanceIterator(context, origin, Statics.NO_POSITION, SystemGeneric.STRUCTURAL) : this
				.<T> inheritanceStructuralIterator(context, origin);
		return !readPhantom ? Statics.<T> nullFilter(iterator) : iterator;
	}

	public <T extends Generic> Iterator<T> concreteIterator(Context context, Holder origin, int basePos, boolean readPhantom) {
		Iterator<T> iterator = null;
		boolean noInheritance = ((GenericImpl) origin).safeIsEnabled(context, getNoInheritanceSystemProperty(context));
		if (((GenericImpl) origin).safeIsEnabled(context, getMultiDirectionalSystemProperty(context))) {
			Iterator<T>[] iterators = new Iterator[origin.getComponentsSize()];
			for (basePos = 0; basePos < iterators.length; basePos++)
				iterators[basePos] = noInheritance ? this.<T> noInheritanceIterator(context, origin, basePos, SystemGeneric.CONCRETE) : this.<T> inheritanceConcreteIterator(context, origin, basePos);
			iterator = new ConcateIterator<T>(iterators);
		} else
			iterator = noInheritance ? this.<T> noInheritanceIterator(context, origin, basePos, SystemGeneric.CONCRETE) : this.<T> inheritanceConcreteIterator(context, origin, basePos);
		return !readPhantom ? Statics.<T> nullFilter(iterator) : iterator;
	}

	private Attribute getNoInheritanceSystemProperty(Context context) {
		return ((AbstractContext) context).<Attribute> find(NoInheritanceSystemProperty.class);
	}

	private Attribute getMultiDirectionalSystemProperty(Context context) {
		return ((AbstractContext) context).<Attribute> find(MultiDirectionalSystemProperty.class);
	}

	private <T extends Generic> Iterator<T> noInheritanceIterator(Context context, final Generic origin, int pos, final int metaLevel) {
		return new AbstractFilterIterator<T>(Statics.NO_POSITION == pos ? this.<T> compositesIterator(context) : this.<T> compositesIterator(context, pos)) {
			@Override
			public boolean isSelected() {
				return next.getMetaLevel() == metaLevel && next.inheritsFrom(origin);
			}
		};
	}

	private <T extends Generic> Iterator<T> inheritanceStructuralIterator(final Context context, final Generic origin) {
		return (Iterator<T>) new AbstractSelectableLeafIterator(context, origin) {
			@Override
			public boolean isSelected(Generic candidate) {
				return candidate.getMetaLevel() <= SystemGeneric.STRUCTURAL && candidate.isAttributeOf(GenericImpl.this);
			}

			@Override
			public boolean isSelectable() {
				return next.isStructural();
			}
		};
	}

	private <T extends Generic> Iterator<T> inheritanceConcreteIterator(final Context context, final Generic origin, final int pos) {
		return (Iterator<T>) new AbstractSelectableLeafIterator(context, origin) {

			@Override
			protected Iterator<Generic> children(final Generic father) {
				return new AbstractFilterIterator<Generic>(((GenericImpl) father).directInheritingsIterator(context)) {
					@Override
					public boolean isSelected() {
						boolean selected = ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos);
						if (selected && ((GenericImpl) next).isPseudoStructural(pos))
							if (context instanceof CacheImpl)
								((GenericImpl) next).project((Cache) context, pos, ((CacheImpl) context).findPrimaryByValue(((GenericImpl) next.getImplicit()).supers[0], null, SystemGeneric.CONCRETE));
						return selected;
					}
				};
			}

			@Override
			public boolean isSelectable() {
				return next.isConcrete();
			}

			@Override
			public final boolean isSelected(Generic candidate) {
				throw new IllegalStateException();
			}
		};
	}

	private void project(final Cache cache, final int pos, Generic phantom) {
		Iterator<Object[]> cartesianIterator = new CartesianIterator(iterables(cache, pos));
		while (cartesianIterator.hasNext()) {
			Generic[] components = (Generic[]) cartesianIterator.next();
			if (!findPhantom(cache, phantom, components))
				((CacheImpl) cache).bind(getImplicit(), true, this, components);
		}
	}

	private Iterable<Generic>[] iterables(final Cache cache, final int pos) {
		final Iterable<Generic>[] targetProjectionIterable = new Iterable[components.length];
		for (int i = 0; i < components.length; i++) {
			final int column = i;
			targetProjectionIterable[i] = new Iterable<Generic>() {
				@Override
				public Iterator<Generic> iterator() {
					return pos != column && components[column].isStructural() ? ((GenericImpl) components[column]).allInstancesIterator(cache) : new SingletonIterator<Generic>(components[column]);
				}
			};
		}
		return targetProjectionIterable;

	}

	private boolean findPhantom(Cache cache, Generic phantom, Generic[] components) {
		return phantom != null && ((CacheImpl) cache).fastFind(phantom, Statics.insertFirst(phantom, supers), components) != null;
	}

	boolean safeIsEnabled(Context context, Attribute attribute) {
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(context, attribute) {
			@Override
			public boolean isSelected(Generic candidate) {
				return (candidate.getMetaLevel() <= SystemGeneric.CONCRETE) && candidate.isAttributeOf(GenericImpl.this);
			}

			@Override
			public boolean isSelectable() {
				return next.isConcrete();
			}
		};
		return iterator.hasNext();
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
		s += "Value          : " + getValue() + "\n";
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
		if (isPrimary()) {
			Serializable value = getValue();
			return value instanceof Class ? ((Class<?>) value).getSimpleName() : value != null ? value.toString() : "null";
		}
		return Arrays.toString(getPrimariesArray() /* supers */) + "/" + toString(components);
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
		return (T) getInternalMeta(getMetaLevel() == 0 ? 0 : getMetaLevel() - 1);
	}

	private Generic getInternalMeta(int instanciationLevel) {
		return getMetaLevel() == instanciationLevel ? this : ((GenericImpl) supers[supers.length - 1]).getInternalMeta(instanciationLevel);
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
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return instancesIterator(context);
			}
		};
	}

	private <T extends Generic> Iterator<T> instancesIterator(Context context) {
		return Statics.<T> levelFilter(GenericImpl.this.<T> directInheritingsIterator(context), SystemGeneric.CONCRETE);
	}

	@Override
	public <T extends Generic> T getInstanceByValue(Context context, final Serializable value) {
		return Statics.unambigousFirst(Statics.<T> valueFilter(GenericImpl.this.<T> instancesIterator(context), value));
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

		InstanceValueClassConstraint instanceClass = clazz.getAnnotation(InstanceValueClassConstraint.class);
		if (instanceClass != null)
			setConstraintClass(cache, instanceClass.value());

		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			enablePropertyConstraint(cache);

		if (clazz.getAnnotation(SingularInstanceConstraint.class) != null)
			enableSingularInstanceConstraint(cache);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				enableSingularConstraint(cache, axe);
	}

	Map<Integer, Integer> getPositions(Generic... components) {
		return new Positions(components);
	}

	private class Positions extends LinkedHashMap<Integer, Integer> {
		private static final long serialVersionUID = 1715235949973772843L;
		Collection<Integer> forbidden = values();

		private Positions(Generic[] components) {
			for (int i = 0; i < components.length; i++)
				put(i, getComponentPos(components[i]));
		}

		public int getComponentPos(Generic generic) {
			int i;
			for (i = 0; i < getComponentsSize(); i++)
				if (!forbidden.contains(i) && (generic == null ? GenericImpl.this.equals(getComponent(i)) : generic.inheritsFrom(getComponent(i))))
					return i;
			// if (isStructural())
			// throw new IllegalStateException("Unable to find component position for : " + generic + " in " + GenericImpl.this);
			while (forbidden.contains(i))
				i++;
			return i;
		}
	}

	public int getFirstComponentPos(Generic component, Generic[] targets) {
		return getPositions(Statics.insertFirst(component, targets)).get(component);
	}

	@Override
	public boolean isRemovable(Cache cache) {
		return cache.isRemovable(this);
	}

	@Override
	public Snapshot<Generic> getRefenrentialIntegrities(Cache cache) {
		return cache.getReferentialIntegrities(this);
	}

	/*********************************************/
	/**************** PHANTOM ********************/
	/*********************************************/

	@Override
	public <T extends Generic> T cancel(Cache cache, Holder attribute, Generic... targets) {
		return cancel(cache, attribute, getBasePos(attribute, targets), targets);
	}

	@Override
	public <T extends Generic> T cancel(Cache cache, Holder attribute, int basePos, Generic... targets) {
		if (equals(attribute.getComponent(basePos)))
			throw new IllegalStateException("Only inherited attributes can be cancelled");

		Iterator<Holder> holders;
		if (attribute.isStructural()) {
			holders = GenericImpl.this.<Holder> attributesIterator(cache, (Attribute) attribute, false);
			while (holders.hasNext()) {
				Holder holder = holders.next();
				if (!holder.equals(attribute))
					throw new IllegalStateException("Unable to cancel an attribute with projection : " + holder);
			}
		}
		holders = GenericImpl.this.<Holder> concreteIterator(cache, (Attribute) attribute, basePos, false);
		while (holders.hasNext()) {
			Holder holder = holders.next();
			if (!holder.equals(attribute))
				throw new IllegalStateException("Unable to cancel an attribute with projection : " + holder);
		}
		return internalCancel(cache, attribute, basePos);
	}

	private <T extends Generic> T internalCancel(Cache cache, Holder attribute, int basePos) {
		Generic implicit = attribute.isStructural() ? getEngine().bindPrimary(cache, null, SystemGeneric.STRUCTURAL, true) : ((GenericImpl) attribute.getMeta()).bindPrimary(cache, null, SystemGeneric.CONCRETE, true);
		return bind(cache, implicit, attribute, basePos, Statics.truncate(basePos, ((GenericImpl) attribute).components));
	}

	@Override
	public void restore(Cache cache, Holder attribute, Generic... targets) {
		restore(cache, attribute, getBasePos(attribute, targets));
	}

	@Override
	public void restore(final Cache cache, final Holder attribute, final int basePos, Generic... targets) {
		Iterator<Holder> holders = attribute.isStructural() ? GenericImpl.this.<Holder> attributesIterator(cache, (Attribute) attribute, true) : GenericImpl.this.<Holder> concreteIterator(cache, (Attribute) attribute, basePos, true);
		while (holders.hasNext()) {
			Holder holder = holders.next();
			Generic[] holderSupers = ((GenericImpl) holder).supers;
			if (equals(holder.getComponent(basePos)) && ((GenericImpl) holder).getValue() == null && Objects.equals(holderSupers[holderSupers.length - 1].getValue(), attribute.getValue()))
				holder.remove(cache);
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
		return ((CacheImpl) cache).bind(bindPrimary(cache, value, SystemGeneric.CONCRETE, true), false, this, new Generic[dim]);
	}

	@Override
	public <T extends Node> T setNode(Cache cache, Serializable value, Generic... targets) {
		Holder attribute = getMeta();
		return bind(cache, ((GenericImpl) attribute).bindPrimary(cache, value, SystemGeneric.CONCRETE, true), attribute, getBasePos(attribute, targets), targets);
	}

	@Override
	public <T extends Node> T setSubNode(Cache cache, Serializable value, Generic... targets) {
		Holder implicit = bindPrimary(cache, value, SystemGeneric.CONCRETE, true);
		return bind(cache, implicit, this, getBasePos(this, targets), targets);
	}

	@Override
	public <T extends Node> Snapshot<T> getChildren(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return childrenIterator(context);
			}
		};
	}

	<T extends Generic> Iterator<T> childrenIterator(Context context) {
		Tree attribute = getMeta();
		return thisFilter(GenericImpl.this.<T> concreteIterator(context, attribute, getBasePos(attribute, Statics.EMPTY_GENERIC_ARRAY), false));
	}

	@Override
	public <T extends Node> T getChild(Context context, Serializable value) {
		Tree attribute = getMeta();
		return Statics.unambigousFirst(Statics.<T> valueFilter(this.<T> thisFilter(GenericImpl.this.<T> concreteIterator(context, attribute, getBasePos(attribute, Statics.EMPTY_GENERIC_ARRAY), value == null)), value));
	}

	public <T extends Generic> Iterator<T> thisFilter(Iterator<T> concreteIterator) {
		return new AbstractFilterIterator<T>(concreteIterator) {
			@Override
			public boolean isSelected() {
				return !GenericImpl.this.equals(next);
			}
		};
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

	public <T extends Generic> T enableSystemProperty(Cache cache, Class<?> systemPropertyClass) {
		return enableSystemProperty(cache, systemPropertyClass, Statics.BASE_POSITION);
	}

	public <T extends Generic> T disableSystemProperty(Cache cache, Class<?> systemPropertyClass) {
		return disableSystemProperty(cache, systemPropertyClass, Statics.BASE_POSITION);
	}

	public <T extends Generic> T enableSystemProperty(Cache cache, Class<?> systemPropertyClass, int basePos) {
		setBooleanSystemProperty(cache, cache.<Attribute> find(systemPropertyClass), basePos, !systemPropertyClass.getAnnotation(SystemGeneric.class).defaultBehavior());
		return (T) this;
	}

	public <T extends Generic> T disableSystemProperty(Cache cache, Class<?> systemPropertyClass, int basePos) {
		setBooleanSystemProperty(cache, cache.<Attribute> find(systemPropertyClass), basePos, systemPropertyClass.getAnnotation(SystemGeneric.class).defaultBehavior());
		return (T) this;
	}

	private <T extends Generic> T setBooleanSystemProperty(Cache cache, Attribute systemProperty, int basePos, boolean defaultBehavior) {
		if (defaultBehavior)
			return setHolder(cache, systemProperty, basePos);
		else {
			Link holder = getHolderByValue(cache, systemProperty, basePos);
			if (holder != null)
				if (equals(holder.getBaseComponent())) {
					holder.remove(cache);
					return setBooleanSystemProperty(cache, systemProperty, basePos, defaultBehavior);
				} else
					cancel(cache, holder, basePos);
		}
		return null;
	}

	public boolean isBooleanSystemPropertyEnabled(Context context, Class<? extends BooleanSystemProperty> systemPropertyClass) {
		return isBooleanSystemPropertyEnabled(context, systemPropertyClass, Statics.BASE_POSITION);
	}

	public boolean isBooleanSystemPropertyEnabled(Context context, Class<? extends BooleanSystemProperty> systemPropertyClass, int basePos) {
		boolean defaultBehavior = systemPropertyClass.getAnnotation(SystemGeneric.class).defaultBehavior();
		return getHolderByValue(context, ((AbstractContext) context).<Attribute> find(systemPropertyClass), basePos) == null ? defaultBehavior : !defaultBehavior;
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
		return isBooleanSystemPropertyEnabled(context, MultiDirectionalSystemProperty.class);
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
		return isBooleanSystemPropertyEnabled(context, CascadeRemoveSystemProperty.class, basePos);
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
		return isBooleanSystemPropertyEnabled(context, ReferentialIntegritySystemProperty.class, basePos);
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
		return isBooleanSystemPropertyEnabled(context, SingularConstraintImpl.class);
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
		return isBooleanSystemPropertyEnabled(context, SingularConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Generic> T enableSizeConstraint(Cache cache, final int basePos, Integer size) {
		Attribute sizeConstraint = cache.<Attribute> find(SizeConstraintImpl.class);
		T holder = setBooleanSystemProperty(cache, sizeConstraint, basePos, !SizeConstraintImpl.class.getAnnotation(SystemGeneric.class).defaultBehavior());
		holder.setHolder(cache, sizeConstraint.getAttribute(cache, SizeConstraintImpl.SIZE), size);
		return (T) this;
	}

	@Override
	public <T extends Generic> T disableSizeConstraint(Cache cache, final int basePos) {
		return disableSystemProperty(cache, SizeConstraintImpl.class, basePos);
	}

	@Override
	public Integer getSizeConstraint(Cache cache, final int basePos) {
		Attribute sizeConstraint = cache.<Attribute> find(SizeConstraintImpl.class);
		Link valuedHolder = getHolderByValue(cache, sizeConstraint, basePos);
		if (valuedHolder == null)
			return null;
		return valuedHolder.getValue(cache, sizeConstraint.getAttribute(cache, SizeConstraintImpl.SIZE));
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
		return isBooleanSystemPropertyEnabled(context, PropertyConstraintImpl.class);
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
		return isBooleanSystemPropertyEnabled(context, RequiredConstraintImpl.class);
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
		return isBooleanSystemPropertyEnabled(context, RequiredConstraintImpl.class, basePos);
	}

	// @Override
	// public <T extends Type> T enableNotNullConstraint(Cache cache) {
	// return enableSystemProperty(cache, NotNullConstraintImpl.class);
	// }
	//
	// @Override
	// public <T extends Type> T disableNotNullConstraint(Cache cache) {
	// return disableSystemProperty(cache, NotNullConstraintImpl.class);
	// }
	//
	// @Override
	// public boolean isNotNullConstraintEnabled(Context context) {
	// return isBooleanSystemPropertyEnabled(context, NotNullConstraintImpl.class);
	// }

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
		return isBooleanSystemPropertyEnabled(context, UniqueConstraintImpl.class);
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
		return isBooleanSystemPropertyEnabled(context, VirtualConstraintImpl.class);
	}

	// TODO pas comme les autres contraintes
	@Override
	public Class<?> getConstraintClass(Cache cache) {
		Holder holder = getHolder(cache, cache.<Attribute> find(InstanceClassConstraintImpl.class));
		return (Class<?>) (holder == null ? Object.class : holder.getValue());
	}

	// TODO pas comme les autres contraintes
	@Override
	public <T extends Type> T setConstraintClass(Cache cache, Class<?> constraintClass) {
		return setHolder(cache, cache.<Attribute> find(InstanceClassConstraintImpl.class), constraintClass);
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
		return isBooleanSystemPropertyEnabled(context, SingularInstanceConstraintImpl.class);
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
		return !isBooleanSystemPropertyEnabled(context, NoInheritanceSystemProperty.class);
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

	boolean isPseudoStructural(int basePos) {
		if (!isConcrete())
			return false;
		for (int i = 0; i < components.length; i++)
			if (i != basePos && components[i].isStructural())
				return true;
		return false;
	}

	// TODO KK
	@Override
	public <T extends Node> Snapshot<T> getRoots(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return Statics.<T> rootFilter(GenericImpl.this.<T> instancesIterator(context));
			}
		};
	}

	// TODO KK
	@Override
	public <T extends Node> T getRootByValue(Context context, Serializable value) {
		return Statics.unambigousFirst(Statics.<T> rootFilter(Statics.<T> valueFilter(GenericImpl.this.<T> instancesIterator(context), value)));
	}

}
