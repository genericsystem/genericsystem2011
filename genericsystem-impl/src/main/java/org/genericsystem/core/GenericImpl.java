package org.genericsystem.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.NoInheritance;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.PropertyConstraint;
import org.genericsystem.annotation.constraints.SingletonConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.constraints.UniqueValueConstraint;
import org.genericsystem.annotation.constraints.VirtualConstraint;
import org.genericsystem.constraints.InstanceClassConstraintImpl;
import org.genericsystem.constraints.MetaLevelConstraintImpl;
import org.genericsystem.constraints.PropertyConstraintImpl;
import org.genericsystem.constraints.RequiredConstraintImpl;
import org.genericsystem.constraints.SingletonConstraintImpl;
import org.genericsystem.constraints.SingularConstraintImpl;
import org.genericsystem.constraints.SizeConstraintImpl;
import org.genericsystem.constraints.UniqueValueConstraintImpl;
import org.genericsystem.constraints.VirtualConstraintImpl;
import org.genericsystem.core.UnsafeGList.Components;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.genericsystem.core.UnsafeVertex.Vertex;
import org.genericsystem.exception.AmbiguousSelectionException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractConcateIterator;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractProjectorAndFilterIterator;
import org.genericsystem.iterator.CartesianIterator;
import org.genericsystem.iterator.CountIterator;
import org.genericsystem.iterator.SingletonIterator;
import org.genericsystem.map.AbstractMapProvider;
import org.genericsystem.map.AbstractMapProvider.AbstractExtendedMap;
import org.genericsystem.map.AxedPropertyClass;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.PropertiesMapProvider;
import org.genericsystem.map.SystemPropertiesMapProvider;
import org.genericsystem.snapshot.FunctionalSnapshot;
import org.genericsystem.snapshot.SingletonSnapshot;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.NoInheritanceProperty;
import org.genericsystem.systemproperties.NoReferentialIntegritySystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 *
 */
@SuppressWarnings("unchecked")
public class GenericImpl implements Generic, Type, Link, Relation, Holder, Attribute {

	protected static Logger log = LoggerFactory.getLogger(GenericImpl.class);

	private LifeManager lifeManager;

	private Vertex vertex;

	public Vertex vertex() {
		return vertex;
	}

	public HomeTreeNode homeTreeNode() {
		return vertex.homeTreeNode();
	}

	@Override
	public Supers getSupers() {
		return vertex.supers();
	}

	@Override
	public Components getComponents() {
		return vertex.components();
	}

	@Override
	public boolean fastValueEquals(Generic generic) {
		return homeTreeNode().equals(((GenericImpl) generic).homeTreeNode());
	}

	public HomeTreeNode bindInstanceNode(Serializable value) {
		return homeTreeNode().bindInstanceNode(value);
	}

	public HomeTreeNode findInstanceNode(Serializable value) {
		return homeTreeNode().findInstanceNode(value);
	}

	final GenericImpl initialize(UnsafeVertex uVertex) {
		return restore(uVertex, null, Long.MAX_VALUE, 0L, Long.MAX_VALUE);
	}

	final GenericImpl restore(UnsafeVertex uVertex, Long designTs, long birthTs, long lastReadTs, long deathTs) {
		vertex = new Vertex(this, uVertex);

		lifeManager = new LifeManager(designTs == null ? getEngine().pickNewTs() : designTs, birthTs, lastReadTs, deathTs);

		for (Generic superGeneric : getSupers()) {
			if (this.equals(superGeneric) && !isEngine())
				getCurrentCache().rollback(new IllegalStateException());
			if ((getMetaLevel() - superGeneric.getMetaLevel()) > 1)
				getCurrentCache().rollback(new IllegalStateException());
			if ((getMetaLevel() - superGeneric.getMetaLevel()) < 0)
				getCurrentCache().rollback(new IllegalStateException());
			assert superGeneric.equals(getMeta()) || superGeneric.getMetaLevel() == getMetaLevel() : "superGeneric " + superGeneric.info() + " getMeta() " + getMeta().info();
			assert superGeneric.equals(getMeta()) || getMeta().inheritsFrom(superGeneric.getMeta()) : getSupers();
		}
		return this;
	}

	<T extends Generic> T plug() {
		Set<Generic> componentsSet = new HashSet<>();
		for (Generic component : getComponents())
			if (componentsSet.add(component))
				((GenericImpl) component).lifeManager.engineComposites.add(this);

		Set<Generic> supersSet = new HashSet<>();
		for (Generic superGeneric : getSupers())
			if (supersSet.add(superGeneric))
				((GenericImpl) superGeneric).lifeManager.engineInheritings.add(this);
		return (T) this;
	}

	<T extends Generic> T unplug() {
		Set<Generic> componentsSet = new HashSet<>();
		for (Generic component : getComponents())
			if (componentsSet.add(component))
				((GenericImpl) component).lifeManager.engineComposites.remove(this);

		Set<Generic> supersSet = new HashSet<>();
		for (Generic superGeneric : getSupers())
			if (supersSet.add(superGeneric))
				((GenericImpl) superGeneric).lifeManager.engineInheritings.remove(this);
		return (T) this;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO clean
		// log.info("FINALIZE " + info());
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
	public EngineImpl getEngine() {
		return (EngineImpl) getSupers().get(0).getEngine();
	}

	@Override
	public boolean isEngine() {
		return false;
	}

	@Override
	public boolean isInstanceOf(Generic meta) {
		return getMetaLevel() - meta.getMetaLevel() == 1 ? this.inheritsFrom(meta) : false;
	}

	@Override
	public <T extends Generic> T getMeta() throws RollbackException {
		return vertex.getMeta();
	}

	@Override
	public int getMetaLevel() {
		return homeTreeNode().getMetaLevel();
	}

	@Override
	public boolean isConcrete() {
		return vertex().isConcrete();
	}

	@Override
	public boolean isStructural() {
		return vertex().isStructural();
	}

	@Override
	public boolean isMeta() {
		return vertex().isMeta();
	}

	@Override
	public boolean isType() {
		return getComponents().size() == 0;
	}

	@Override
	public boolean isAttribute() {
		return getComponents().size() >= 1;
	}

	@Override
	public boolean isAttributeOf(Generic generic) {
		for (Generic component : getComponents())
			if (generic.inheritsFrom(component))
				return true;
		return false;
	}

	@Override
	public boolean isAttributeOf(Generic generic, int basePos) {
		if (basePos < 0 || basePos >= getComponents().size())
			return false;
		return generic.inheritsFrom(getComponents().get(basePos));
	}

	@Override
	public boolean isRelation() {
		return getComponents().size() > 1;
	}

	@Override
	public <S extends Serializable> S getValue() {
		return homeTreeNode().getValue();
	}

	@Override
	public <T extends Serializable> FunctionalSnapshot<T> getValues(final Holder attribute) {
		return getHolders(attribute).project(holder -> holder.<T> getValue());
	}

	@Override
	public <T extends Serializable> T getValue(Holder attribute) {
		Link holder = getHolder(attribute);
		return holder != null ? holder.<T> getValue() : null;
	}

	@Override
	public <T extends Holder> T setValue(Holder attribute, Serializable value) {
		T holder = setHolder(attribute, value, Statics.CONCRETE);
		assert value == null || getValues(attribute).contains(value) : "holder : " + holder.info() + " value : " + value + " => " + getValues(attribute);
		return holder;
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, Generic... targets) {
		return setLink(relation, value, getBasePos(relation), targets);
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, int basePos, Generic... targets) {
		return setLink(relation, value, basePos, Statics.CONCRETE, targets);
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, int basePos, int metaLevel, Generic... targets) {
		return setHolder(relation, value, metaLevel, basePos, targets);
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, Generic... targets) {
		return setHolder(attribute, value, Statics.CONCRETE, targets);
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, int metaLevel, Generic... targets) {
		return setHolder(attribute, value, metaLevel, getBasePos(attribute), targets);
	}

	@Override
	public <T extends Holder> T addHolder(Holder attribute, int basePos, Serializable value, Generic... targets) {
		return addHolder(attribute, value, basePos, Statics.CONCRETE, targets);
	}

	@Override
	public <T extends Holder> T addHolder(Holder attribute, Serializable value, int basePos, int metaLevel, Generic... targets) {
		return bind(metaLevel == attribute.getMetaLevel() ? attribute.getMeta() : attribute, value, null, attribute, Statics.EMPTY_GENERIC_ARRAY, basePos, true, targets);
	}

	public <T extends Holder> T bind(Generic meta, Serializable value, Class<?> specializationClass, Holder directSuper, Generic[] satifies, int basePos, boolean existsException, Generic... targets) {
		return getCurrentCache().bind(meta, value, specializationClass, directSuper, satifies, existsException, Statics.insertIntoArray(this, targets, basePos));
	}

	@Override
	public void cancel(Holder holder) {
		internalClear(unambigousFirst(holdersSnapshot(holder, Statics.CONCRETE, getBasePos(holder))));
		internalCancel(unambigousFirst(holdersSnapshot(holder, Statics.CONCRETE, getBasePos(holder))), Statics.CONCRETE, getBasePos(holder));
	}

	@Override
	public void cancelAll(Holder attribute, Generic... targets) {
		int basePos = getBasePos(attribute);
		clearAll(attribute, targets);
		FunctionalSnapshot<Holder> holders = this.<Holder> holdersSnapshot(attribute, Statics.CONCRETE, basePos, targets);
		for (Holder holder : holders)
			internalCancel(holder, Statics.CONCRETE, basePos);
	}

	@Override
	public void clear(Holder holder) {
		internalClear(unambigousFirst(holdersSnapshot(holder, Statics.CONCRETE, getBasePos(holder))));
	}

	@Override
	public void clearAll(Holder attribute, Generic... targets) {
		FunctionalSnapshot<Holder> holders = this.<Holder> holdersSnapshot(attribute, Statics.CONCRETE, getBasePos(attribute), targets);
		for (Holder holder : holders)
			internalClear(holder);
	}

	private void internalCancel(Holder attribute, int metaLevel, int basePos) {
		if (attribute != null)
			bind(metaLevel == attribute.getMetaLevel() ? attribute.getMeta() : attribute, null, null, attribute, Statics.EMPTY_GENERIC_ARRAY, basePos, false);
	}

	private void internalClear(Holder holder) {
		if (holder != null && equals(holder.getBaseComponent()))
			holder.remove();
	}

	@Override
	public int getBasePos(Holder attribute) {
		Iterator<Integer> iterator = positionsIterator(attribute);
		return iterator.hasNext() ? iterator.next() : Statics.BASE_POSITION;
	}

	@Override
	public <T extends Holder> FunctionalSnapshot<T> getHolders(final Holder attribute, final Generic... targets) {
		return getHolders(attribute, getBasePos(attribute), targets);
	}

	@Override
	public <T extends Holder> FunctionalSnapshot<T> getHolders(final Holder attribute, final int basePos, final Generic... targets) {
		return holdersSnapshot((Attribute) attribute, Statics.CONCRETE, basePos, targets);
	}

	@Override
	public <T extends Link> FunctionalSnapshot<T> getLinks(final Relation relation, final Generic... targets) {
		return getLinks(relation, getBasePos(relation), targets);
	}

	@Override
	public <T extends Link> FunctionalSnapshot<T> getLinks(final Relation relation, final int basePos, final Generic... targets) {
		return linksSnapshot(relation, basePos, targets);
	}

	@Override
	public <T extends Generic> Snapshot<T> getTargets(Relation relation) {
		return getTargets(relation, Statics.BASE_POSITION, Statics.TARGET_POSITION);
	}

	@Override
	public <T extends Generic> Snapshot<T> getTargets(Relation relation, int basePos, final int targetPos) {
		return getLinks(relation, basePos).project(element -> element.getComponent(targetPos));
	}

	// TODO To Delete
	public <T extends Holder> Iterator<T> holdersIterator(Holder attribute, Generic... targets) {
		return this.<T> targetsFilter(GenericImpl.this.<T> holdersIterator(Statics.CONCRETE, attribute, getBasePos(attribute)), attribute, targets);
	}

	public <T extends Holder> FunctionalSnapshot<T> holdersSnapshot(Holder attribute, Generic... targets) {
		return holdersSnapshot(attribute, Statics.CONCRETE, getBasePos(attribute), targets);
	}

	public <T extends Holder> FunctionalSnapshot<T> holdersSnapshot(Holder attribute, int metaLevel, int basePos, Generic... targets) {
		return this.<T> targetsFilter(GenericImpl.this.<T> holdersSnapshot(metaLevel, attribute, basePos), attribute, targets);
	}

	@Override
	public <T extends Holder> T getHolder(Holder attribute, Generic... targets) {
		return getHolder(Statics.CONCRETE, attribute, getBasePos(attribute), targets);
	}

	@Override
	public <T extends Holder> T getHolder(int metaLevel, Holder attribute, Generic... targets) {
		return getHolder(metaLevel, attribute, getBasePos(attribute), targets);
	}

	@Override
	public <T extends Holder> T getHolder(int metaLevel, Holder attribute, int basePos, Generic... targets) {
		return this.unambigousFirst(this.<T> holdersSnapshot(attribute, metaLevel, basePos, targets));
	}

	public <T extends Holder> T getHolderByValue(Holder attribute, Serializable value, final Generic... targets) {
		return getHolderByValue(Statics.CONCRETE, attribute, value, targets);
	}

	public <T extends Holder> T getHolderByValue(int metaLevel, Holder attribute, Serializable value, final Generic... targets) {
		return getHolderByValue(metaLevel, attribute, value, getBasePos(attribute), targets);
	}

	public <T extends Holder> T getHolderByValue(int metaLevel, Holder attribute, Serializable value, int basePos, final Generic... targets) {
		FunctionalSnapshot<T> snapshot = this.<T> holdersSnapshot(attribute, metaLevel, basePos, targets);
		return unambigousFirst(snapshot.filter(next -> Objects.equals(value, next.getValue())));
	}

	@Override
	public <T extends Link> T getLink(Link relation, int basePos, Generic... targets) {
		return this.unambigousFirst(this.<T> linksSnapshot(relation, basePos, targets));
	}

	private <T extends Link> FunctionalSnapshot<T> linksSnapshot(final Link relation, final int basePos, final Generic... targets) {
		return GenericImpl.this.<T> holdersSnapshot(relation, Statics.CONCRETE, basePos, targets).filter(next -> next.isRelation());
	}

	@Override
	public <T extends Link> T getLink(Link relation, Generic... targets) {
		return getLink(relation, getBasePos(relation), targets);
	}

	// TODO TO DELETE - ( is called by 'dependencidesIterator()' l.486 - see also class ConcateIterator)
	public <T extends Generic> Iterator<T> inheritingsIterator() {
		return getCurrentCache().inheritingsIterator(this);
	}

	public <T extends Generic> FunctionalSnapshot<T> getInheritingsSnapshot() {
		return () -> getCurrentCache().inheritingsIterator(this);
	}

	// TODO FINISH MIGRATION IN SNAPSHOT (ConcateIterator -> ConcateSnapshot before deleting) + TO DELETE
	public <T extends Generic> Iterator<T> dependenciesIterator() {
		return new ConcateIterator<T>(this.<T> inheritingsIterator(), this.<T> compositesIterator());
	}

	public <T extends Generic> FunctionalSnapshot<T> dependenciesSnapshot() {
		return () -> new ConcateIterator<T>(this.<T> inheritingsIterator(), this.<T> compositesIterator());
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getInheritings() {
		return () -> inheritingsIterator();
	}

	// TODO TO DELETE - ( is called by 'dependencidesIterator()' l.486 - see also class ConcateIterator)
	public <T extends Generic> Iterator<T> compositesIterator() {
		return getCurrentCache().compositesIterator(this);
	}

	public <T extends Generic> FunctionalSnapshot<T> compositesSnapshot() {
		return () -> getCurrentCache().compositesIterator(this);
	}

	// TODO TO DELETE
	public <T extends Generic> Iterator<T> compositesIterator(final int pos) {
		return new AbstractFilterIterator<T>(this.<T> compositesIterator()) {
			@Override
			public boolean isSelected() {
				return GenericImpl.this.equals(((Holder) next).getComponent(pos));
			}
		};
	}

	public <T extends Generic> FunctionalSnapshot<T> compositesSnapshot(final int pos) {
		return this.<T> compositesSnapshot().filter(next -> GenericImpl.this.equals(((Holder) next).getComponent(pos)));
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getComposites() {
		return () -> compositesIterator();
	}

	@Override
	public <T extends Attribute> FunctionalSnapshot<T> getAttributes() {
		return getAttributes(getCurrentCache().getMetaAttribute());
	}

	@Override
	public <T extends Attribute> FunctionalSnapshot<T> getAttributes(final Attribute attribute) {
		return this.<T> holdersSnapshot(Statics.STRUCTURAL, attribute, Statics.MULTIDIRECTIONAL);
	}

	@Override
	public <T extends Attribute> T getAttribute(Serializable value, Generic... targets) {
		Attribute metaAttribute = getCurrentCache().getMetaAttribute();
		return this.<T> unambigousFirst(this.<T> targetsFilter(this.<T> getAttributes().filter(next -> Objects.equals(value, next.getValue())), metaAttribute, targets));
	}

	@Override
	public <T extends Relation> Snapshot<T> getRelations() {
		return getAttributes();
	}

	public <T extends Generic> T reFind() {
		return getCurrentCache().reFind(this);
	}

	@Override
	public <T extends Relation> T getRelation(Serializable value, Generic... targets) {
		return getAttribute(value, targets);
	}

	@Override
	public <T extends Attribute> T getProperty(Serializable value, Generic... targets) {
		return getAttribute(value, targets);
	}

	@Override
	public <T extends Attribute> T setProperty(Serializable value, Generic... targets) {
		return setAttribute(value, targets).enablePropertyConstraint();
	}

	public <T extends Attribute> T setSubProperty(Attribute property, Serializable value, Generic... targets) {
		return setSubAttribute(property, value, targets).enablePropertyConstraint();
	}

	@Override
	public <T extends Relation> T setRelation(Serializable value, Generic... targets) {
		return setAttribute(value, targets);
	}

	public <T extends Attribute> T addSubRelation(Relation relation, Serializable value, Generic... targets) {
		return addSubAttribute(relation, value, targets);
	}

	public <T extends Attribute> T setSubRelation(Relation relation, Serializable value, Generic... targets) {
		return setSubAttribute(relation, value, targets);
	}

	@Override
	public <T extends Attribute> T setAttribute(Serializable value, Generic... targets) {
		return setHolder(getEngine(), value, Statics.STRUCTURAL, Statics.BASE_POSITION, targets);
	}

	@Override
	public <T extends Attribute> T addAttribute(Serializable value, Generic... targets) {
		return addHolder((Holder) getEngine(), value, Statics.BASE_POSITION, Statics.STRUCTURAL, targets);
	}

	public <T extends Relation> T setSubAttribute(Attribute attribute, Serializable value, Generic... targets) {
		T holder = setHolder(attribute, value, Statics.STRUCTURAL, getBasePos(attribute), targets);
		assert holder == null || holder.inheritsFrom(attribute) : holder.info() + attribute.info();
		return holder;
	}

	public <T extends Relation> T addSubAttribute(Attribute attribute, Serializable value, Generic... targets) {
		T holder = addHolder(attribute, value, getBasePos(attribute), Statics.STRUCTURAL, targets);
		assert holder.inheritsFrom(attribute) : holder.info();
		return holder;
	}

	public <T extends Relation> T addSubProperty(Attribute attribute, Serializable value, Generic... targets) {
		return addSubAttribute(attribute, value, targets).enableSingularConstraint();
	}

	public <T extends Holder> T setHolder(Class<?> specializationClass, Holder attribute, Serializable value, int metaLevel, int basePos, Generic... targets) {
		return this.<T> bind(attribute.getMetaLevel() >= metaLevel ? attribute.getMeta() : attribute, value, specializationClass, attribute, Statics.EMPTY_GENERIC_ARRAY, basePos, false, targets);
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, int metaLevel, int basePos, Generic... targets) {
		return this.<T> setHolder(null, attribute, value, metaLevel, basePos, targets);
	}

	@Override
	public <T extends Holder> T flag(Holder attribute, Generic... targets) {
		return setHolder(attribute, Statics.FLAG, Statics.CONCRETE, getBasePos(attribute), targets);
	}

	@Override
	public <T extends Generic> T addAnonymousInstance(Generic... components) {
		return addInstance(getEngine().pickNewAnonymousReference(), components);
	}

	@Override
	public <T extends Generic> T setAnonymousInstance(Generic... components) {
		return setInstance(getEngine().pickNewAnonymousReference(), components);
	}

	@Override
	public <T extends Generic> T addInstance(Serializable value, Generic... components) {
		return getCurrentCache().bind(this, value, null, this, Statics.EMPTY_GENERIC_ARRAY, true, components);
	}

	@Override
	public <T extends Generic> T setInstance(Serializable value, Generic... components) {
		return getCurrentCache().bind(this, value, null, this, Statics.EMPTY_GENERIC_ARRAY, false, components);
	}

	@Override
	public <T extends Type> T addSubType(Serializable value) {
		return addSubType(value, Statics.EMPTY_GENERIC_ARRAY, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T addSubType(Serializable value, Generic[] satifies, Generic... components) {
		if (isMeta())
			getCurrentCache().rollback(new UnsupportedOperationException("Derive a meta is not allowed"));
		return getCurrentCache().bind(getMeta(), value, null, this, satifies, true, components);
	}

	@Override
	public <T extends Type> T setSubType(Serializable value) {
		return setSubType(value, Statics.EMPTY_GENERIC_ARRAY, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T setSubType(Serializable value, Generic[] satifies, Generic... components) {
		if (isMeta())
			getCurrentCache().rollback(new UnsupportedOperationException("Derive a meta is not allowed"));
		return getCurrentCache().bind(getMeta(), value, null, this, satifies, false, components);
	}

	@Override
	public <T extends Link> T bind(Link relation, Generic... targets) {
		return flag(relation, targets);
	}

	private class TakenPositions extends ArrayList<Integer> {

		private static final long serialVersionUID = 1777313486204962418L;
		private final int max;

		public TakenPositions(int max) {
			this.max = max;
		}

		public int getFreePosition(Generic component) throws RollbackException {
			int freePosition = 0;
			while (contains(freePosition) || (freePosition < getComponents().size() && !component.inheritsFrom(getComponents().get(freePosition))))
				freePosition++;
			if (freePosition >= max)
				getCurrentCache().rollback(new IllegalStateException("Unable to find a valid position for : " + component.info() + " in : " + getComponents() + " " + getComponents().get(0).info()));
			add(freePosition);
			return freePosition;
		}
	}

	// TODO KK result should be an arrayList and elements should be added on demand
	Generic[] sortAndCheck(Generic... components) {
		TakenPositions takenPositions = new TakenPositions(components.length);
		Generic[] result = new Generic[components.length];
		for (Generic component : components)
			result[takenPositions.getFreePosition(component == null ? GenericImpl.this : component)] = component;
		return result;
	}

	// TODO TO DELETE
	public <T extends Generic> Iterator<T> holdersIterator(int level, Holder origin, int basePos) {
		if (Statics.STRUCTURAL == level)
			basePos = Statics.MULTIDIRECTIONAL;
		return ((Attribute) origin).isInheritanceEnabled() ? this.<T> inheritanceIterator(level, origin, basePos) : this.<T> noInheritanceIterator(level, basePos, origin);
	}

	public <T extends Generic> FunctionalSnapshot<T> holdersSnapshot(int level, Holder origin, int basePos) {
		if (Statics.STRUCTURAL == level)
			basePos = Statics.MULTIDIRECTIONAL;
		return ((Attribute) origin).isInheritanceEnabled() ? this.<T> inheritanceSnapshot(level, origin, basePos) : this.<T> noInheritanceSnapshot(level, basePos, origin);
	}

	// TODO TO DELETE
	private <T extends Generic> Iterator<T> noInheritanceIterator(final int metaLevel, int pos, final Generic origin) {
		return new AbstractFilterIterator<T>(Statics.MULTIDIRECTIONAL == pos ? this.<T> compositesIterator() : this.<T> compositesIterator(pos)) {
			@Override
			public boolean isSelected() {
				return next.getMetaLevel() == metaLevel && next.inheritsFrom(origin);
			}
		};
	}

	private <T extends Generic> FunctionalSnapshot<T> noInheritanceSnapshot(final int metaLevel, int pos, final Generic origin) {
		return (Statics.MULTIDIRECTIONAL == pos ? this.<T> compositesSnapshot() : this.<T> compositesSnapshot(pos)).filter(next -> next.getMetaLevel() == metaLevel && next.inheritsFrom(origin));
	}

	// TODO TO DELETE
	private <T extends Generic> Iterator<T> inheritanceIterator(final int level, final Generic origin, final int pos) {
		return new AbstractFilterIterator<T>(this.<T> getInternalInheritings(level, origin, pos).iterator()) {
			@Override
			public boolean isSelected() {
				return level == next.getMetaLevel() && (pos != Statics.MULTIDIRECTIONAL ? ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos) : ((GenericImpl) next).isAttributeOf(GenericImpl.this));
			}
		};
	}

	private <T extends Generic> FunctionalSnapshot<T> inheritanceSnapshot(final int level, final Generic origin, final int pos) {
		return this.<T> getInternalInheritings(level, origin, pos).filter(
				next -> level == next.getMetaLevel() && (pos != Statics.MULTIDIRECTIONAL ? ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos) : ((GenericImpl) next).isAttributeOf(GenericImpl.this)));
	}

	private Iterator<Generic> supersIterator(final Generic origin) {
		return new AbstractFilterIterator<Generic>(getSupers().iterator()) {

			@Override
			public boolean isSelected() {
				return !GenericImpl.this.equals(next) && origin.isAttributeOf(next);
			}
		};
	}

	private FunctionalSnapshot<Generic> supersSnapshot(final Generic origin) {
		return ((FunctionalSnapshot<Generic>) () -> getSupers().iterator()).filter(next -> !GenericImpl.this.equals(next) && origin.isAttributeOf(next));
	}

	// DONT TOUCH ! is not touched..
	// private <T extends Generic> Iterator<T> inheritanceIterator2(final int level, final Generic origin, final int pos) {
	// return new AbstractFilterIterator<T>(new SpecializedMainInheritance(origin, level).<T> specialize()) {
	// @Override
	// public boolean isSelected() {
	// return level == next.getMetaLevel() && (pos == Statics.MULTIDIRECTIONAL || ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos));
	// }
	// };
	// }

	// DONT TOUCH TO JAVA8 VERSION TOO !
	private <T extends Generic> FunctionalSnapshot<T> inheritanceIterator2(final int level, final Generic origin, final int pos) {
		return new SpecializedMainInheritance(origin, level).<T> specialize().filter(next -> level == next.getMetaLevel() && (pos == Statics.MULTIDIRECTIONAL || ((GenericImpl) next).isAttributeOf(GenericImpl.this, pos)));
	}

	// DONT TOUCH !
	private class SpecializedMainInheritance extends HashSet<Generic> {

		private static final long serialVersionUID = -8308697833901246495L;
		private final int level;
		private final Generic origin;
		private final CompositesIndex compositesIndex = new CompositesIndex();

		private SpecializedMainInheritance(final Generic origin, final int level) {
			this.level = level;
			this.origin = origin;
		}

		// TODO MIGRATION DONE - inheritanceIterator2() migrated too but old version is commented and intact (!)
		private <T extends Generic> FunctionalSnapshot<T> specialize() {
			return ((FunctionalSnapshot<T>) () -> new MainInheritanceProjector(GenericImpl.this).<T> project()).filter(next -> !contains(next));
		}

		private class CompositesIndex extends HashMap<Generic, Map<Generic, Set<Generic>>> {

			private static final long serialVersionUID = -6404067063383874676L;

			// TODO NOT TESTED : MUST BE OK NORMALY
			// private <T extends Generic> FunctionalSnapshot<T> getIndexedCompositeSnapshot(Generic base, final Generic index) {
			// Map<Generic, Set<Generic>> indexedCompositeMap = get(base);
			// if (indexedCompositeMap == null) {
			// put(base, indexedCompositeMap = new HashMap<>());
			// FunctionalSnapshot<T> snapshot = ((GenericImpl) base).<T> compositesSnapshot().filter(next -> next.getMetaLevel() <= level);
			// for (int i = 0; i < snapshot.size(); i++)
			// {
			// final T next = snapshot.get(i);
			// for (Generic superGeneric : next.getSupers())
			// {
			// Set<Generic> indexedCompositeSet = indexedCompositeMap.get(superGeneric);
			// if (indexedCompositeSet == null)
			// indexedCompositeMap.put(superGeneric, indexedCompositeSet) = new HashSet<Generic>());
			// indexedCompositeSet.add(next);
			// }
			// }
			// }
			// //TODO Remove Iterator used for return
			// Set<Generic> indexedCompositeSet = indexedCompositeMap.get(index);
			// if (indexedCompositeSet == null)
			// return Collections.emptyIterator();
			// return () -> indexedCompositeSet.iterator();
			// }
			// }

			// TODO TO FINISH - IS MIGRATED (remove iterator in return)
			private <T extends Generic> Iterator<T> getIndexedCompositeIterator(Generic base, final Generic index) {
				Map<Generic, Set<Generic>> indexedCompositeMap = get(base);
				if (indexedCompositeMap == null) {
					put(base, indexedCompositeMap = new HashMap<>());
					Iterator<T> iterator = new AbstractFilterIterator<T>(((GenericImpl) base).<T> compositesIterator()) {
						@Override
						public boolean isSelected() {
							return next.getMetaLevel() <= level;
						}
					};
					while (iterator.hasNext()) {
						final T next = iterator.next();
						for (Generic superGeneric : next.getSupers()) {
							Set<Generic> indexedCompositeSet = indexedCompositeMap.get(superGeneric);
							if (indexedCompositeSet == null)
								indexedCompositeMap.put(superGeneric, indexedCompositeSet = new HashSet<Generic>());
							indexedCompositeSet.add(next);
						}
					}
				}

				Set<Generic> indexedCompositeSet = indexedCompositeMap.get(index);
				if (indexedCompositeSet == null)
					return Collections.emptyIterator();
				return (Iterator<T>) indexedCompositeSet.iterator();
			}
		}

		private <T extends Generic> Iterator<T> indexedCompositeIterator(Generic base, final Generic index) {
			return compositesIndex.getIndexedCompositeIterator(base, index);
		}

		private class MainInheritanceProjector extends HashSet<Generic> {

			private static final long serialVersionUID = 2189650244025973386L;
			private final Generic base;

			public MainInheritanceProjector(final Generic base) {
				this.base = base;
			}

			// TODO TO MIGRATE + TO DELETE
			private <T extends Generic> Iterator<T> project() {
				return projectIterator(this.<T> projectFromSupersIterator());
			}

			// TODO TO MIGRATE + TO DELETE
			private <T extends Generic> Iterator<T> projectFromSupersIterator() {
				if (!origin.isAttributeOf(base))
					return Collections.emptyIterator();
				Iterator<Generic> supersIterator = ((GenericImpl) base).supersIterator(origin);
				if (!supersIterator.hasNext())
					return new SingletonIterator<T>((T) origin);
				return new AbstractConcateIterator<Generic, T>(supersIterator) {
					@Override
					protected Iterator<T> getIterator(final Generic superGeneric) {
						return new MainInheritanceProjector((superGeneric)).project();
					}
				};
			}

			// TODO TO MIGRATE + TO DELETE
			private <T extends Generic> Iterator<T> projectIterator(Iterator<T> iteratorToProject) {
				return new AbstractConcateIterator<T, T>(iteratorToProject) {
					@Override
					protected Iterator<T> getIterator(final T index) {
						Iterator<T> indexIterator = indexedCompositeIterator(base, index);
						if (indexIterator.hasNext())
							SpecializedMainInheritance.this.add(index);
						if (add(index))
							if (indexIterator.hasNext())
								return new ConcateIterator<>(new SingletonIterator<T>(index), projectIterator(indexIterator));
							else
								return new SingletonIterator<T>(index);
						return Collections.<T> emptyIterator();
					}
				};
			}
		}
	}

	private class Inheritings<T extends Generic> extends LinkedHashSet<T> {

		private static final long serialVersionUID = 6333116882294134638L;

		private final int pos;
		private final int maxLevel;

		private Inheritings(int maxLevel, Generic origin, int pos) {
			this.pos = pos;
			this.maxLevel = maxLevel;
			for (Generic superGeneric : getSupers())
				if (!GenericImpl.this.equals(superGeneric))
					for (T inheriting : (((GenericImpl) superGeneric).<T> getInternalInheritings(maxLevel, origin, pos)))
						add(inheriting);
			for (T composite : (GenericImpl.this.<T> getComposites()))
				if (composite.getMetaLevel() <= maxLevel && composite.inheritsFrom(origin))
					add(composite);
		}

		boolean isCandidateToProject(int pos, Generic candidate) {
			if (maxLevel != candidate.getMetaLevel() || GenericImpl.this.equals(((GenericImpl) candidate).getComponents().get(pos)))
				return false;
			Iterator<T> iterator = iterator();
			while (iterator.hasNext()) {
				Generic next = iterator.next();
				UnsafeComponents candidateComponents = Statics.replace(pos, ((GenericImpl) candidate).getComponents(), GenericImpl.this);
				Generic candidateMeta = candidate.getMeta();
				if (((GenericImpl) next).homeTreeNode().equals(((GenericImpl) candidate).homeTreeNode()) && next.getMeta().equals(candidateMeta) && candidateComponents.equals(Statics.replace(pos, ((GenericImpl) next).getComponents(), GenericImpl.this)))
					return true;
			}
			return false;
		}

		@Override
		public boolean add(T candidate) {
			Iterator<T> iterator = iterator();
			while (iterator.hasNext()) {
				Generic next = iterator.next();
				if (candidate.inheritsFrom(next) && !candidate.equals(next))
					iterator.remove();
				else if (next.inheritsFrom(candidate))
					return false;
			}
			if (pos != Statics.MULTIDIRECTIONAL) {
				if (((GenericImpl) candidate).isPseudoStructural(pos))
					((GenericImpl) candidate).project(pos);
				if (isCandidateToProject(pos, candidate))
					((GenericImpl) candidate).getReplacedComponentBuilder(pos, GenericImpl.this).simpleBind(candidate.getClass(), false, true);
			}
			return super.add(candidate);
		}
	}

	private <T extends Generic> FunctionalSnapshot<T> getInternalInheritings(int level, Generic origin, int pos) {
		return () -> new Inheritings<T>(level, origin, pos).iterator();
	}

	public void project() {
		project(Statics.MULTIDIRECTIONAL);
	}

	public void project(final int pos) {
		Iterator<Generic[]> cartesianIterator = new CartesianIterator<>(projections(pos));
		while (cartesianIterator.hasNext()) {
			final UnsafeComponents components = new UnsafeComponents(cartesianIterator.next());
			if (this.unambigousFirst(getAllInheritingsSnapshotWithoutRoot().filter(next -> ((GenericImpl) next).inheritsFrom(((GenericImpl) next).filterToProjectVertex(components, pos)))) == null)
				getReplacedComponentsBuilder(components).bind(null, false, true);
		}
	}

	private Iterable<Generic>[] projections(final int pos) {
		final Iterable<Generic>[] projections = new Iterable[getComponents().size()];
		for (int i = 0; i < projections.length; i++) {
			int column = i;
			projections[i] = () -> pos != column && getComponents().get(column).isStructural() ? ((GenericImpl) getComponents().get(column)).allInstancesIterator() : new SingletonIterator<Generic>(getComponents().get(column));
		}
		return projections;
	}

	@Override
	public boolean inheritsFrom(Generic generic) {
		if (equals(generic))
			return true;
		if (generic.isEngine())
			return true;
		if (getDesignTs() < ((GenericImpl) generic).getDesignTs())
			return false;
		for (Generic directSuper : getSupers())
			if (((GenericImpl) directSuper).inheritsFrom(generic))
				return true;
		return false;
	}

	public boolean isSuperOf2(Generic subGeneric) {
		if (GenericImpl.this.equals(subGeneric))
			return true;
		if (subGeneric.isEngine())
			return isEngine();
		for (Generic directSuper : ((GenericImpl) subGeneric).getSupers())
			if (isSuperOf2(directSuper))
				return true;
		return false;
	}

	@Override
	public boolean inheritsFromAll(Generic... generics) {
		for (Generic generic : generics)
			if (!inheritsFrom(generic))
				return false;
		return true;
	}

	public boolean inheritsFromAll(UnsafeGList generics) {
		for (Generic generic : generics)
			if (!inheritsFrom(generic))
				return false;
		return true;
	}

	public boolean inheritsFrom(UnsafeVertex superUVertex) {
		if (equiv(superUVertex))
			return true;
		if (superUVertex.metaLevel() > getMetaLevel())
			return false;
		if (getComponents().size() < superUVertex.components().size())
			return false;
		for (Generic subSuper : getSupers())
			if (subSuper.inheritsFrom(this))
				return true;
		if (getComponents().size() > superUVertex.components().size()) {
			for (int i = 0; i < getComponents().size(); i++)
				if (isSuperOf(superUVertex, vertex.truncateComponent(i)))
					return true;
			return false;
		}
		// Statics.logTimeIfCurrentThreadDebugged("AAA");
		Generic subVertexMeta = getMeta();
		if (isConcrete() && subVertexMeta.equals(superUVertex.getMeta()))
			for (int pos = 0; pos < getComponents().size(); pos++)
				if (((GenericImpl) subVertexMeta).isSingularConstraintEnabled(pos) /* && !subVertexMeta.isReferentialIntegrity(pos) */)
					if (getComponent(pos).inheritsFrom(superUVertex.components().get(pos)))
						if (!getComponent(pos).equals(superUVertex.components().get(pos)))
							return true;
		// Statics.logTimeIfCurrentThreadDebugged("BBB");
		for (int i = 0; i < getComponents().size(); i++)
			if (superUVertex.components().get(i) != null) {
				if (!getComponents().get(i).inheritsFrom(superUVertex.components().get(i)))
					return false;
			} else {
				if (!getComponents().get(i).inheritsFrom(this))
					if (!((GenericImpl) getComponents().get(i)).inheritsFrom(superUVertex))
						return false;
			}
		// Statics.logTimeIfCurrentThreadDebugged("CCC");
		if (isConcrete() && subVertexMeta.equals(superUVertex.getMeta()))
			if (((GenericImpl) subVertexMeta).isPropertyConstraintEnabled())
				if (!getComponents().equals(superUVertex.components()))
					return true;
		// Statics.logTimeIfCurrentThreadDebugged("DDD");

		if (!(homeTreeNode().inheritsFrom(superUVertex.homeTreeNode()) || (getMetaLevel() == superUVertex.metaLevel() && subVertexMeta.inheritsFrom(superUVertex.getMeta()) && Objects
				.equals(homeTreeNode().getValue(), superUVertex.homeTreeNode().getValue()))))
			// if (!homeTreeNode().inheritsFrom(superUVertex.homeTreeNode()))
			return false;
		// Statics.logTimeIfCurrentThreadDebugged("EEE");
		if (!inheritsFromAll(superUVertex.supers()))
			return false;
		return true;
	}

	private static boolean isSuperOf(UnsafeVertex superUVertex, UnsafeVertex subUVertex) {
		if (superUVertex.homeTreeNode().equals(subUVertex.homeTreeNode()) && superUVertex.supers().equals(subUVertex.supers()) && superUVertex.components().equals(subUVertex.components()))
			return true;
		if (superUVertex.metaLevel() > subUVertex.metaLevel())
			return false;
		if (subUVertex.components().size() < superUVertex.components().size())
			return false;
		for (Generic subSuper : subUVertex.supers())
			if (((GenericImpl) subSuper).inheritsFrom(superUVertex))
				return true;
		// for (Generic superGeneric : superUVertex.supers())
		// if (!((GenericImpl) superGeneric).isSuperOf(subUVertex))
		// return false;
		if (subUVertex.components().size() > superUVertex.components().size()) {
			for (int i = 0; i < subUVertex.components().size(); i++)
				if (isSuperOf(superUVertex, subUVertex.truncateComponent(i)))
					return true;
			return false;
		}
		Generic subVertexMeta = subUVertex.getMeta();
		if (subUVertex.isConcrete() && subVertexMeta.equals(superUVertex.getMeta()))
			for (int pos = 0; pos < subUVertex.components().size(); pos++)
				if (((GenericImpl) subVertexMeta).isSingularConstraintEnabled(pos) /* && !subVertexMeta.isReferentialIntegrity(pos) */)
					if (subUVertex.components().get(pos).inheritsFrom(superUVertex.components().get(pos)))
						if (!subUVertex.components().get(pos).equals(superUVertex.components().get(pos)))
							return true;
		for (int i = 0; i < subUVertex.components().size(); i++)
			if (superUVertex.components().get(i) != null) {
				if (!subUVertex.components().get(i).inheritsFrom(superUVertex.components().get(i)))
					return false;
			} else if (!((GenericImpl) subUVertex.components().get(i)).inheritsFrom(superUVertex))
				if (!((GenericImpl) subUVertex.components().get(i)).inheritsFrom(superUVertex))
					return false;
		if (subUVertex.isConcrete() && subVertexMeta.equals(superUVertex.getMeta()))
			if (((GenericImpl) subVertexMeta).isPropertyConstraintEnabled())
				if (!subUVertex.components().equals(superUVertex.components()))
					return true;
		if (!(subUVertex.homeTreeNode().inheritsFrom(superUVertex.homeTreeNode()) || (subUVertex.metaLevel() == superUVertex.metaLevel() && subVertexMeta.inheritsFrom(superUVertex.getMeta()) && Objects.equals(subUVertex.homeTreeNode().getValue(),
				superUVertex.homeTreeNode().getValue()))))

			// if (!subUVertex.homeTreeNode().inheritsFrom(superUVertex.homeTreeNode()))
			return false;
		for (Generic superUVertexSuper : superUVertex.supers())
			if (!((GenericImpl) superUVertexSuper).isSuperOf(subUVertex))
				return false;
		return true;
	}

	public boolean isSuperOf(UnsafeVertex subUVertex) {
		if (isEngine())
			return true;
		if (equiv(subUVertex))
			return true;
		if (getMetaLevel() > subUVertex.metaLevel())
			return false;
		if (subUVertex.components().size() < getComponents().size())
			return false;
		for (Generic subSuper : subUVertex.supers())
			if (subSuper.inheritsFrom(this))
				return true;
		// for (Generic superGeneric : getSupers())
		// if (!((GenericImpl) superGeneric).isSuperOf(subUVertex))
		// return false;
		if (subUVertex.components().size() > getComponents().size()) {
			for (int i = 0; i < subUVertex.components().size(); i++)
				if (isSuperOf(subUVertex.truncateComponent(i)))
					return true;
			return false;
		}
		Generic subVertexMeta = subUVertex.getMeta();
		if (subUVertex.isConcrete() && subVertexMeta.equals(getMeta()))
			for (int pos = 0; pos < subUVertex.components().size(); pos++)
				if (((GenericImpl) subVertexMeta).isSingularConstraintEnabled(pos) /* && !subVertexMeta.isReferentialIntegrity(pos) */)
					if (subUVertex.components().get(pos).inheritsFrom(getComponent(pos)))
						if (!subUVertex.components().get(pos).equals(getComponent(pos)))
							return true;

		for (int i = 0; i < subUVertex.components().size(); i++)
			if (subUVertex.components().get(i) != null) {
				if (!subUVertex.components().get(i).inheritsFrom(getComponent(i)))
					return false;
			} else {
				if (!equals(getComponents().get(i)))
					if (!(((GenericImpl) getComponents().get(i)).isSuperOf(subUVertex)))
						return false;
			}
		if (subUVertex.isConcrete() && subVertexMeta.equals(getMeta()))
			if (((GenericImpl) subVertexMeta).isPropertyConstraintEnabled())
				if (!subUVertex.components().equals(getComponents()))
					return true;
		if (!(subUVertex.homeTreeNode().inheritsFrom(homeTreeNode()) || (subUVertex.metaLevel() == getMetaLevel() && subVertexMeta.inheritsFrom(getMeta()) && Objects.equals(subUVertex.homeTreeNode().getValue(), homeTreeNode().getValue()))))

			// if (!subUVertex.homeTreeNode().inheritsFrom(homeTreeNode()))
			return false;

		return true;
	}

	@Override
	public void remove() {
		remove(RemoveStrategy.NORMAL);
	}

	@Override
	public void remove(RemoveStrategy removeStrategy) {
		getCurrentCache().remove(this, removeStrategy);
	}

	@Override
	public boolean isAlive() {
		return getCurrentCache().isAlive(this);
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
	public void log() {
		log.info(info());
	}

	@Override
	public String info() {
		String s = "\n******************************" + System.identityHashCode(this) + "******************************\n";
		s += " Name        : " + toString() + "\n";
		s += " HomeTreeNode: " + homeTreeNode() + "\n";
		s += " Meta        : " + getMeta() + " (" + System.identityHashCode(getMeta()) + ")\n";
		s += " MetaLevel   : " + Statics.getMetaLevelString(getMetaLevel()) + "\n";
		s += " Category    : " + getCategoryString() + "\n";
		s += " Class       : " + getClass().getSimpleName() + "\n";
		s += "**********************************************************************\n";
		for (Generic superGeneric : getSupers())
			s += " Super       : " + superGeneric + " (" + System.identityHashCode(superGeneric) + ")\n";
		for (Generic component : getComponents())
			s += " Component   : " + component + " (" + System.identityHashCode(component) + ")\n";
		s += "**********************************************************************\n";

		// for (Attribute attribute : getAttributes())
		// if (!(attribute.getValue() instanceof Class) /* || !Constraint.class.isAssignableFrom((Class<?>) attribute.getValue()) */) {
		// s += ((GenericImpl) attribute).getCategoryString() + "   : " + attribute + " (" + System.identityHashCode(attribute) + ")\n";
		// for (Holder holder : getHolders(attribute))
		// s += "                          ----------> " + ((GenericImpl) holder).getCategoryString() + " : " + holder + "\n";
		// }
		// s += "**********************************************************************\n";
		s += "design date : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDesignTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "birth date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getBirthTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "death date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDeathTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		s += "**********************************************************************\n";

		return s;
	}

	@Override
	public String toString() {
		Serializable value = getValue();
		if (null == value)
			return "null" + (getSupers().size() >= 2 ? "[" + getSupers().get(1) + "]" : "");
		return value instanceof Class ? ((Class<?>) value).getSimpleName() : value.toString();
	}

	public String toCategoryString() {
		return "(" + getCategoryString() + ") " + toString();
	}

	public String getCategoryString() throws RollbackException {
		int metaLevel = getMetaLevel();
		int dim = getComponents().size();
		switch (metaLevel) {
		case Statics.META:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "MetaType";
			case Statics.ATTRIBUTE_SIZE:
				return "MetaAttribute";
			case Statics.RELATION_SIZE:
				return "MetaRelation";
			default:
				return "MetaNRelation";
			}
		case Statics.STRUCTURAL:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "Type";
			case Statics.ATTRIBUTE_SIZE:
				return "Attribute";
			case Statics.RELATION_SIZE:
				return "Relation";
			default:
				return "NRelation";
			}
		case Statics.CONCRETE:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "Instance";
			case Statics.ATTRIBUTE_SIZE:
				return "Holder";
			case Statics.RELATION_SIZE:
				return "Link";
			default:
				return "NLink";
			}
		default:
			getCurrentCache().rollback(new IllegalStateException());
			return null;// Uneachable
		}
	}

	@Override
	public <T extends Generic> T getBaseComponent() {
		return getComponent(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Generic> T getTargetComponent() {
		return getComponent(Statics.TARGET_POSITION);
	}

	// TODO KK try to remove this method calls where it is possible!
	@Override
	public <T extends Generic> T getComponent(int componentPos) {
		return getComponents().size() <= componentPos || componentPos < 0 ? null : (T) getComponents().get(componentPos);
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getAllInstances() {
		return this.<T> allInheritingsAboveSnapshot().filter(next -> next.getMetaLevel() == getMetaLevel() + 1);
	}

	// TODO TO DELETE
	public <T extends Generic> Iterator<T> allInstancesIterator() {
		return Statics.levelFilter(this.<T> allInheritingsAboveIterator(getMetaLevel() + 1), getMetaLevel() + 1);
	}

	public <T extends Generic> FunctionalSnapshot<T> getAllInstancesSnapshot() {
		return () -> allInstancesIterator();
	}

	private <T extends Generic> FunctionalSnapshot<T> allInheritingsAboveSnapshot() {
		return () -> this.<T> allInheritingsAboveIterator(getMetaLevel() + 1);
	}

	// TO DELETE
	private <T extends Generic> Iterator<T> allInheritingsAboveIterator(final int metaLevel) {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {

			private static final long serialVersionUID = 7164424160379931253L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return new AbstractFilterIterator<Generic>(((GenericImpl) node).inheritingsIterator()) {
					@Override
					public boolean isSelected() {
						return next.getMetaLevel() <= metaLevel;
					}
				};
			}
		};
	}

	private <T extends Generic> FunctionalSnapshot<T> getAllInheritingsAboveSnapshot(final int metaLevel) {
		return (FunctionalSnapshot<T>) new AbstractPreTreeSnapshot<Generic>(this) {

			private static final long serialVersionUID = 2056157222479853791L;

			@Override
			public FunctionalSnapshot<Generic> children(Generic node) {
				return ((GenericImpl) node).getInheritingsSnapshot().filter(next -> next.getMetaLevel() <= metaLevel);
			}
		}.getSnapshot();
	}

	// TODO KK
	public abstract class AbstractPreTreeSnapshot<T> extends HashSet<T> {

		private static final long serialVersionUID = -518282246760045090L;

		class Entry {
			int index = 0;

			FunctionalSnapshot<T> functionalSnapshot;

			public Entry(FunctionalSnapshot<T> functionalSnapshot) {
				this.functionalSnapshot = functionalSnapshot;
			}

			public T getAndIncrement() {
				return functionalSnapshot.get(index++);
			}

			public boolean hasNext() {
				return index < functionalSnapshot.size();
			}
		}

		protected Deque<Entry> deque = new ArrayDeque<Entry>();

		public AbstractPreTreeSnapshot(T rootNode) {
			deque.push(new Entry(new SingletonSnapshot<T>(rootNode)));
		}

		public FunctionalSnapshot<T> getSnapshot() {
			Entry entry = deque.peek();
			final T node = entry.getAndIncrement();
			if (!entry.hasNext())
				deque.pop();

			FunctionalSnapshot<T> children = children(node).filter(next -> add(next));
			if (!children.isEmpty())
				deque.push(new Entry(children));

			return children;
		}

		public abstract FunctionalSnapshot<T> children(T node);
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getInstances() {
		return () -> instancesIterator();
	}

	public <T extends Generic> Iterator<T> instancesIterator() {
		return new AbstractFilterIterator<T>(GenericImpl.this.<T> allInheritingsAboveIterator(getMetaLevel() + 1)) {
			@Override
			public boolean isSelected() {
				return GenericImpl.this.equals(next.getMeta());
			}
		};
	}

	public <T extends Generic> T getGeneric(final Serializable value, final Generic... components) {
		FunctionalSnapshot<T> snapshot = (components.length > 0 ? ((GenericImpl) components[0]).<T> compositesSnapshot() : this.<T> getInstances()).filter(next -> Objects.equals(value, next.getValue()));
		return unambigousFirst(this.<T> componentsFilter(snapshot, this, components).filter(next -> next.getMeta().equals(GenericImpl.this)));
	}

	// TODO TO DELETE
	<T extends Generic> Iterator<T> targetsFilter(Iterator<T> iterator, Holder attribute, Generic... targets) {
		final List<Integer> positions = ((GenericImpl) attribute).getComponentsPositions(Statics.insertFirst(this, targets));
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

	<T extends Generic> FunctionalSnapshot<T> targetsFilter(FunctionalSnapshot<T> snapshot, Holder attribute, Generic... targets) {
		final List<Integer> positions = ((GenericImpl) attribute).getComponentsPositions(Statics.insertFirst(this, targets));
		return snapshot.filter(next -> {
			for (int i = 0; i < targets.length; i++)
				if (!targets[i].equals(((Holder) next).getComponent(positions.get(i + 1))))
					return false;
			return true;
		});
	}

	// TODO TO MIGRATE + TO DELETE
	<T extends Generic> Iterator<T> componentsFilter(Iterator<T> iterator, Holder attribute, Generic... components) {
		final List<Integer> positions = ((GenericImpl) attribute).getComponentsPositions(components);
		return new AbstractFilterIterator<T>(iterator) {
			@Override
			public boolean isSelected() {
				for (int i = 0; i < components.length; i++)
					if (!components[i].equals(((Holder) next).getComponent(positions.get(i))))
						return false;
				return true;
			}
		};
	}

	<T extends Generic> FunctionalSnapshot<T> componentsFilter(FunctionalSnapshot<T> snapshot, Holder attribute, Generic... components) {
		final List<Integer> positions = ((GenericImpl) attribute).getComponentsPositions(components);
		return snapshot.filter(next -> {
			for (int i = 0; i < components.length; i++)
				if (!components[i].equals(((Holder) next).getComponent(positions.get(i))))
					return false;
			return true;
		});
	}

	// TODO KK supers are necessary for get instance from meta !!!
	@Override
	public <T extends Generic> T getInstance(Serializable value, Generic... targets) {
		return (T) unambigousFirst(targetsFilter(getAllInstancesSnapshot().filter(next -> Objects.equals(value, next.getValue())), this, targets));
	}

	@Override
	public <T extends Type> T getSubType(Serializable value) {
		return unambigousFirst(this.<T> getSubTypesSnapshot().filter(next -> Objects.equals(value, next.getValue())));
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getSubTypes() {
		return getSubTypesSnapshot();
	}

	private <T extends Generic> FunctionalSnapshot<T> getSubTypesSnapshot() {
		return GenericImpl.this.<T> getInheritingsSnapshot().filter(next -> next.getMetaLevel() == getMetaLevel());
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getAllSubTypes() {
		return this.<T> getAllInheritingsSnapshotWithoutRoot().filter(next -> next.isStructural());
	}

	// TODO super KK what is this method, what does it do : no components ? no supers ? ???
	@Override
	public <T extends Generic> T getAllSubType(Serializable value) {
		return unambigousFirst((this.<T> getAllInheritingsSnapshotWithoutRoot().filter(next -> next.isStructural()).filter(next -> Objects.equals(value, next.getValue()))));
	}

	@Override
	public <T extends Generic> FunctionalSnapshot<T> getAllSubTypes(final String name) {
		return this.<T> allSubTypesSnapshotWithoutRoot().filter(next -> Objects.equals(name, next.getValue()));
	}

	private <T extends Generic> FunctionalSnapshot<T> allSubTypesSnapshotWithoutRoot() {
		return this.<T> getAllInheritingsSnapshotWithoutRoot().filter(next -> next.isStructural());
	}

	public <T extends Generic> FunctionalSnapshot<T> getAllInheritings() {
		return () -> allInheritingsIterator();
	}

	public <T extends Generic> FunctionalSnapshot<T> getAllInheritingsWithoutRoot() {
		return () -> allInheritingsIteratorWithoutRoot();
	}

	// TODO TO DELETE
	protected <T extends Generic> Iterator<T> allInheritingsIterator() {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {
			private static final long serialVersionUID = 4540682035671625893L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return (((GenericImpl) node).inheritingsIterator());
			}
		};
	}

	// TODO TO DELETE
	<T extends Generic> Iterator<T> allInheritingsIteratorWithoutRoot() {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(GenericImpl.this) {

			{
				next();
			}

			private static final long serialVersionUID = 4540682035671625893L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return (((GenericImpl) node).inheritingsIterator());
			}
		};
	}

	<T extends Generic> FunctionalSnapshot<T> getAllInheritingsSnapshotWithoutRoot() {
		return () -> allInheritingsIteratorWithoutRoot();
	}

	void mountConstraints(Class<?> clazz) {
		if (clazz.getAnnotation(NoInheritance.class) != null)
			disableInheritance();

		if (clazz.getAnnotation(VirtualConstraint.class) != null)
			enableVirtualConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			enableUniqueValueConstraint();

		InstanceValueClassConstraint instanceClass = clazz.getAnnotation(InstanceValueClassConstraint.class);
		if (instanceClass != null)
			setConstraintClass(instanceClass.value());

		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			enablePropertyConstraint();

		if (clazz.getAnnotation(SingletonConstraint.class) != null)
			enableSingletonConstraint();

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				enableSingularConstraint(axe);
	}

	@Override
	public boolean isRemovable() {
		return getCurrentCache().isRemovable(this);
	}

	/*********************************************/
	/************** SYSTEM PROPERTY **************/
	/*********************************************/

	private <T extends Generic> Serializable getSystemPropertyValue(Class<T> propertyClass, int pos) {
		return getSystemPropertiesMap().get(new AxedPropertyClass(propertyClass, pos));
	}

	public <T extends Generic> void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		getSystemPropertiesMap().put(new AxedPropertyClass(propertyClass, pos), value);
	}

	private <T extends Generic> boolean isSystemPropertyEnabled(Class<T> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	public <T extends Generic> Serializable getConstraintValue(Class<T> constraintClass, int pos) {
		return getConstraintsMap().get(new AxedPropertyClass(constraintClass, pos));
	}

	public <T extends Generic> void setConstraintValue(Class<T> constraintClass, int pos, Serializable value) {
		getConstraintsMap().put(new AxedPropertyClass(constraintClass, pos), value);
	}

	public <T extends Generic> boolean isConstraintEnabled(Class<T> constraintClass, int pos) {
		Serializable value = getConstraintValue(constraintClass, pos);
		return null != value && !Boolean.FALSE.equals(value);
	}

	public <T extends Relation> T enableMetaLevelConstraintAttribute() {
		setSystemPropertyValue(MetaLevelConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		return (T) this;
	}

	public <T extends Relation> T disableMetaLevelConstraintAttribute() {
		setSystemPropertyValue(MetaLevelConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		return (T) this;
	}

	@Override
	public <T extends Relation> T enableCascadeRemove(int basePos) {
		setSystemPropertyValue(CascadeRemoveSystemProperty.class, basePos, true);
		return (T) this;
	}

	@Override
	public <T extends Relation> T disableCascadeRemove(int basePos) {
		setSystemPropertyValue(CascadeRemoveSystemProperty.class, basePos, false);
		return (T) this;
	}

	@Override
	public boolean isCascadeRemove(int basePos) {
		return isSystemPropertyEnabled(CascadeRemoveSystemProperty.class, basePos);
	}

	@Override
	public <T extends Relation> T enableInheritance() {
		setSystemPropertyValue(NoInheritanceProperty.class, Statics.BASE_POSITION, false);
		return (T) this;
	}

	@Override
	public <T extends Relation> T disableInheritance() {
		setSystemPropertyValue(NoInheritanceProperty.class, Statics.BASE_POSITION, true);
		return (T) this;
	}

	@Override
	public boolean isInheritanceEnabled() {
		if (!GenericImpl.class.equals(getClass()))
			return !getClass().isAnnotationPresent(NoInheritance.class);
		return !isSystemPropertyEnabled(NoInheritanceProperty.class, Statics.BASE_POSITION);
	}

	@Override
	public <T extends Generic> T enableReferentialIntegrity() {
		return enableReferentialIntegrity(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Generic> T disableReferentialIntegrity() {
		return disableReferentialIntegrity(Statics.BASE_POSITION);
	}

	@Override
	public boolean isReferentialIntegrity() {
		return isReferentialIntegrity(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Generic> T enableReferentialIntegrity(int componentPos) {
		setSystemPropertyValue(NoReferentialIntegritySystemProperty.class, componentPos, false);
		return (T) this;
	}

	@Override
	public <T extends Generic> T disableReferentialIntegrity(int componentPos) {
		setSystemPropertyValue(NoReferentialIntegritySystemProperty.class, componentPos, true);
		return (T) this;
	}

	@Override
	public boolean isReferentialIntegrity(int basePos) {
		return !isSystemPropertyEnabled(NoReferentialIntegritySystemProperty.class, basePos);
	}

	@Override
	public <T extends Type> T enableSingularConstraint() {
		return enableSingularConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T disableSingularConstraint() {
		return disableSingularConstraint(Statics.BASE_POSITION);
	}

	@Override
	public boolean isSingularConstraintEnabled() {
		return isSingularConstraintEnabled(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T enableSingularConstraint(int basePos) {
		setConstraintValue(SingularConstraintImpl.class, basePos, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disableSingularConstraint(int basePos) {
		setConstraintValue(SingularConstraintImpl.class, basePos, false);
		return (T) this;
	}

	@Override
	public boolean isSingularConstraintEnabled(int basePos) {
		return isConstraintEnabled(SingularConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Generic> T enableSizeConstraint(int basePos, Integer size) {
		setConstraintValue(SizeConstraintImpl.class, basePos, size);
		return (T) this;
	}

	@Override
	public <T extends Generic> T disableSizeConstraint(int basePos) {
		setConstraintValue(SizeConstraintImpl.class, basePos, false);
		return (T) this;
	}

	@Override
	public Integer getSizeConstraint(int basePos) {
		Serializable result = getConstraintValue(SizeConstraintImpl.class, basePos);
		return result instanceof Integer ? (Integer) result : null;
	}

	@Override
	public Class<?> getConstraintClass() {
		Serializable value = getConstraintValue(InstanceClassConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		return null == value ? Object.class : (Class<?>) value;
	}

	@Override
	public <T extends Type> T setConstraintClass(Class<?> constraintClass) {
		setConstraintValue(InstanceClassConstraintImpl.class, Statics.MULTIDIRECTIONAL, constraintClass);
		return (T) this;
	}

	@Override
	public <T extends Type> T enablePropertyConstraint() {
		setConstraintValue(PropertyConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disablePropertyConstraint() {
		setConstraintValue(PropertyConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		return (T) this;
	}

	@Override
	public boolean isPropertyConstraintEnabled() {
		return isConstraintEnabled(PropertyConstraintImpl.class, Statics.MULTIDIRECTIONAL);
	}

	@Override
	public <T extends Type> T enableRequiredConstraint() {
		return enableRequiredConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T disableRequiredConstraint() {
		return disableRequiredConstraint(Statics.BASE_POSITION);
	}

	@Override
	public boolean isRequiredConstraintEnabled() {
		return isRequiredConstraintEnabled(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T enableRequiredConstraint(int basePos) {
		setConstraintValue(RequiredConstraintImpl.class, basePos, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disableRequiredConstraint(int basePos) {
		setConstraintValue(RequiredConstraintImpl.class, basePos, false);
		return (T) this;
	}

	@Override
	public boolean isRequiredConstraintEnabled(int basePos) {
		return isConstraintEnabled(RequiredConstraintImpl.class, basePos);
	}

	@Override
	public <T extends Type> T enableUniqueValueConstraint() {
		setConstraintValue(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disableUniqueValueConstraint() {
		setConstraintValue(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		return (T) this;
	}

	@Override
	public boolean isUniqueValueConstraintEnabled() {
		return isConstraintEnabled(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL);
	}

	@Override
	public <T extends Type> T enableVirtualConstraint() {
		setConstraintValue(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disableVirtualConstraint() {
		setConstraintValue(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		return (T) this;
	}

	@Override
	public boolean isVirtualConstraintEnabled() {
		return isConstraintEnabled(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL);
	}

	@Override
	public <T extends Type> T enableSingletonConstraint() {
		setConstraintValue(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		return (T) this;
	}

	@Override
	public <T extends Type> T disableSingletonConstraint() {
		setConstraintValue(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		return (T) this;
	}

	@Override
	public boolean isSingletonConstraintEnabled() {
		return isConstraintEnabled(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL);
	}

	Components nullToSelfComponent(UnsafeComponents components) {
		return components instanceof Components ? (Components) components : new Components(this, components);
	}

	UnsafeComponents selfToNullComponents() {
		List<Generic> result = new ArrayList<>(getComponents());
		for (int i = 0; i < result.size(); i++)
			if (equals(result.get(i)))
				result.set(i, null);
		return new UnsafeComponents(result);
	}

	public boolean equiv(Vertex vertex) {
		return vertex().equiv(vertex);
	}

	// public boolean equivByMeta(Vertex vertex) {
	// return vertex().equivByMeta(vertex);
	// }

	public boolean equiv(UnsafeVertex uVertex) {
		return homeTreeNode().equals(uVertex.homeTreeNode()) && getSupers().equals(uVertex.supers()) && getComponents().equals(nullToSelfComponent(uVertex.components()));
	}

	public boolean equiv(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components) {
		return homeTreeNode().equals(homeTreeNode) && getSupers().equals(supers) && getComponents().equals(nullToSelfComponent(components));
	}

	public boolean equiv(HomeTreeNode homeTreeNode, UnsafeComponents components) {
		return homeTreeNode().equals(homeTreeNode) && getComponents().equals(nullToSelfComponent(components));
	}

	public <T extends Generic> T reBind() {
		return getCurrentCache().reBind(this);
	}

	boolean isPseudoStructural(int basePos) {
		if (!isConcrete())
			return false;
		for (int i = 0; i < getComponents().size(); i++)
			if (i != basePos && getComponents().get(i).isStructural())
				return true;
		return false;
	}

	@Override
	public <T extends Attribute> T addProperty(Serializable value, Generic... targets) {
		return addAttribute(value, targets).enablePropertyConstraint();
	}

	@Override
	public <T extends Relation> T addRelation(Serializable value, Generic... targets) {
		return addAttribute(value, targets);
	}

	@Override
	public <T extends Link> T addLink(Link relation, Serializable value, Generic... targets) {
		return addHolder(relation, value, targets);
	}

	@Override
	public <T extends Holder> T addHolder(Holder attribute, Serializable value, Generic... targets) {
		return addHolder(attribute, getBasePos(attribute), value, targets);
	}

	@Override
	public <T extends Holder> T addValue(Holder attribute, Serializable value) {
		return addHolder(attribute, value);
	}

	@Override
	public <Key extends Serializable, Value extends Serializable> AbstractExtendedMap<Key, Value> getMap(Class<? extends MapProvider> mapClass) {
		return this.<Key, Value> getMap(getCurrentCache().<MapProvider> find(mapClass));
	}

	public <Key extends Serializable, Value extends Serializable> AbstractExtendedMap<Key, Value> getMap(MapProvider mapProvider) {
		return (AbstractExtendedMap<Key, Value>) mapProvider.<Key, Value> getExtendedMap(this);
	}

	@Override
	public <Key extends Serializable, Value extends Serializable> AbstractExtendedMap<Key, Value> getPropertiesMap() {
		return getMap(PropertiesMapProvider.class);
	}

	public AbstractExtendedMap<AxedPropertyClass, Serializable> getConstraintsMap() {
		return getMap(ConstraintsMapProvider.class);
	}

	public AbstractExtendedMap<AxedPropertyClass, Serializable> getSystemPropertiesMap() {
		return getMap(SystemPropertiesMapProvider.class);
	}

	@Override
	public boolean isTree() {
		return this instanceof Tree;
	}

	@Override
	public boolean isRoot() {
		return (this instanceof Node) && equals(getBaseComponent());
	}

	@Override
	public <T extends Generic> T addComponent(Generic newComponent, int pos) {
		return getCurrentCache().addComponent(this, newComponent, pos);
	}

	@Override
	public <T extends Generic> T removeComponent(Generic component, int pos) {
		return getCurrentCache().removeComponent(this, pos);
	}

	@Override
	public <T extends Generic> T addSuper(Generic newSuper) {
		return getCurrentCache().addSuper(this, newSuper);
	}

	public <T extends Generic> T addSupers(Generic... newSupers) {
		return getCurrentCache().addSupers(this, newSupers);
	}

	@Override
	public <T extends Generic> T removeSuper(int pos) {
		return getCurrentCache().removeSuper(this, pos);
	}

	@Override
	public <T extends Generic> T setValue(Serializable value) {
		return getCurrentCache().setValue(this, value);
	}

	public List<Integer> getComponentsPositions(Generic... components) {
		return new ComponentsPositions(components);
	}

	private class ComponentsPositions extends ArrayList<Integer> {
		private static final long serialVersionUID = 1715235949973772843L;

		private ComponentsPositions(Generic[] components) {
			for (int i = 0; i < components.length; i++)
				add(getComponentPos(components[i]));
		}

		public int getComponentPos(Generic generic) {
			int i;
			for (i = 0; i < getComponents().size(); i++)
				if (!contains(i) && (generic == null ? GenericImpl.this.equals(getComponent(i)) : generic.inheritsFrom(getComponent(i)))) {
					return i;
				}
			while (contains(i))
				i++;
			return i;
		}
	}

	public FunctionalSnapshot<Integer> getPositions(final Holder attribute) {
		return () -> positionsIterator(attribute);
	}

	Iterator<Integer> positionsIterator(final Holder attribute) {
		final List<Generic> components = ((GenericImpl) attribute).getComponents();
		return new AbstractFilterIterator<Integer>(new CountIterator(components.size())) {
			@Override
			public boolean isSelected() {
				return GenericImpl.this.inheritsFrom(components.get(next));
			}
		};
	}

	// TODO TO DELETE
	public Iterator<Generic> otherTargetsIterator(final Holder holder) {
		final List<Generic> components = ((GenericImpl) holder).getComponents();
		return new AbstractProjectorAndFilterIterator<Integer, Generic>(new CountIterator(components.size())) {

			@Override
			public boolean isSelected() {
				return !GenericImpl.this.equals(components.get(next)) && !GenericImpl.this.inheritsFrom(components.get(next));
			}

			@Override
			protected Generic project() {
				return components.get(next);
			}
		};
	}

	@Override
	public FunctionalSnapshot<Generic> getOtherTargets(final Holder holder) {
		return () -> otherTargetsIterator(holder);
	}

	public boolean isAutomatic() {
		return getCurrentCache().isAutomatic(this);
	}

	public CacheImpl getCurrentCache() {
		return getEngine().getCurrentCache();
	}

	@Override
	public boolean isMapProvider() {
		return this.getValue() instanceof Class && AbstractMapProvider.class.isAssignableFrom((Class<?>) this.getValue());
	}

	// TODO TO DELETE
	public <T> T unambigousFirst(Iterator<T> iterator) throws RollbackException {
		if (!iterator.hasNext())
			return null;
		T result = iterator.next();
		if (iterator.hasNext()) {
			String message = "" + ((Generic) result).info();
			while (iterator.hasNext())
				message += " / " + ((Generic) iterator.next()).info();
			this.getCurrentCache().rollback(new AmbiguousSelectionException("Ambigous selection : " + message));
		}
		return result;
	}

	public <T> T unambigousFirst(FunctionalSnapshot<T> snapshot) throws RollbackException {
		if (snapshot.size() > 1) {
			String message = "";
			for (T elem : snapshot)
				message += " / " + ((Generic) elem).info();
			this.getCurrentCache().rollback(new AmbiguousSelectionException("Ambigous selection : " + message));
		}
		return snapshot.isEmpty() ? null : snapshot.get(0);
	}

	Class<?> specializeInstanceClass(Class<?> specializationClass) {
		InstanceGenericClass instanceClass = getClass().getAnnotation(InstanceGenericClass.class);
		if (instanceClass != null)
			if (specializationClass == null || specializationClass.isAssignableFrom(instanceClass.value()))
				specializationClass = instanceClass.value();
			else
				assert instanceClass.value().isAssignableFrom(specializationClass);
		return specializationClass;
	}

	@Override
	public boolean isSystem() {
		return getClass().isAnnotationPresent(SystemGeneric.class);
	}

	// TODO kk ?
	UnsafeVertex filterToProjectVertex(UnsafeComponents components, int pos) {
		return new UnsafeVertex(homeTreeNode(), getSupers(), Statics.replace(pos, components, getComponent(pos)));
	}

	GenericBuilder getUpdatedValueVertex(HomeTreeNode newHomeTreeNode) {
		return new GenericBuilder(newHomeTreeNode, getSupers(), selfToNullComponents(), false);
	}

	GenericBuilder createNewBuilder(Serializable value, Generic directSuper, Generic[] satifies, Generic[] components) {
		return createNewBuilder(bindInstanceNode(value), directSuper, satifies, components);
	}

	GenericBuilder createNewBuilder(HomeTreeNode homeTreeNode, Generic directSuper, Generic[] satifies, Generic[] components) {
		return new GenericBuilder(homeTreeNode, new Supers(Statics.insertFirst(directSuper, satifies)), new UnsafeComponents(((GenericImpl) directSuper).sortAndCheck(components)), true);
	}

	GenericBuilder getInsertedComponentVertex(Generic newComponent, int pos) {
		return new GenericBuilder(homeTreeNode(), getSupers(), Statics.insertIntoComponents(newComponent, selfToNullComponents(), pos), true);
	}

	GenericBuilder getTruncatedComponentVertex(int pos) {
		return new GenericBuilder(homeTreeNode(), getSupers(), Statics.truncate(pos, selfToNullComponents()), false);
	}

	GenericBuilder getReplacedComponentBuilder(int pos, Generic newComponent) {
		return getReplacedComponentsBuilder(Statics.replace(pos, selfToNullComponents(), newComponent));
	}

	GenericBuilder getReplacedComponentsBuilder(UnsafeComponents components) {
		return new GenericBuilder(homeTreeNode(), getSupers(), components, true);
	}

	GenericBuilder getInsertedSuperVertex(Generic... newSupers) {
		return new GenericBuilder(homeTreeNode(), Statics.insertsFristSupers(Arrays.asList(newSupers), getSupers()), selfToNullComponents(), true);
	}

	GenericBuilder getTruncatedSuperVertex(int pos) {
		return new GenericBuilder(homeTreeNode(), Statics.truncate(pos, getSupers()), selfToNullComponents(), false);
	}

}
