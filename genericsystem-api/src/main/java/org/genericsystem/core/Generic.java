package org.genericsystem.core;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.generic.Relation;

/**
 * <p>Everything in Generic System. Every entity present and managed by Generic System is
 * <tt>Generic</tt>. Generic is the node of the graphe.</p>
 * 
 * <p>Threre are three kinds of generics in Generic System:
 * <dl>
 * 	<dt>Meta<dt><dd>- Abstract entities which define meta-model of Generic System.<dd>
 * 	<dt>Structurals<dt><dd>- Models for concretes. On this level we find types, attributes and relations.<dd>
 * 	<dt>Concrete<dt><dd>- Concrete entities which store relevant data.<dd>
 * </dl></p>
 * 
 * <p>All generics are managed by <tt>Engine</tt>.</p>
 * 
 * <p>The <tt>Generic</tt> interface defines methods to determine the properties of managed entity and
 * to manipulate it's relations.</p>
 * 
 * <p>This interface is part of <tt>Generic System Core</tt>.</p>
 */
public interface Generic extends Comparable<Generic> {

	/**
	 * Returns the <tt>Engine</tt>.
	 * 
	 * @return the <tt>Engine</tt>.
	 */
	Engine getEngine();

	/**
	 * Returns <tt>true</tt> if this generic is <tt>Engine</tt>.
	 * 
	 * @return <tt>true</tt> if this is <tt>Engine</tt>.
	 */
	boolean isEngine();

	/**
	 * Returns <tt>true</tt> if this generic is direct instance of the model passed as parameter.
	 * Model in parameters the mostly often is an <tt>Type</tt> but it can be other instance.
	 * 
	 * @param model - the supposed model for current generic.
	 * 
	 * @return <tt>true</tt> if this is the direct instance of model.
	 */
	boolean isInstanceOf(Generic model);

	/**
	 * Returns instantiation level of the generic. Generic System allows to create infinite meta
	 * levels by inheretings of concretes. Three meta levels actually important to know in Generic
	 * System:
	 * <dl>
	 * 	<dt>0</dt><dd>- Level of Meta-Objects (Engine, MetaAttribute and MetaRelation);</dd>
	 * 	<dt>1</dt><dd>- Level of Structurals (types, attributes, relations);</dd>
	 * 	<dt>>= 2</dt><dd>- Level of Concreets (instances, holders, links).</dd>
	 * </dl>
	 * 
	 * @return the instantiation level.
	 */
	int getMetaLevel();

	/**
	 * Returns true if this generic is a Structural <tt>Type</tt>.
	 * 
	 * @return true if this is a <tt>Type</tt>.
	 */
	boolean isType();

	/**
	 * Returns true if this generic is a Structural <tt>Attribute</tt> or <tt>Relation</tt>.
	 * 
	 * @return true if the generic is an <tt>Attribute</tt> or <tt>Relation</tt>.
	 */
	boolean isAttribute();

	/**
	 * Return true if this generic is an <tt>Attribute</tt> but not <tt>Relation</tt>.
	 * 
	 * @return true if this generic is an <tt>Attribute</tt> but not <tt>Relation</tt>.
	 */
	boolean isReallyAttribute();

	/**
	 * Returns true if this generic has at least two components.
	 * 
	 * @return true if this generic has at least two components.
	 */
	boolean isRelation();

	/**
	 * Returns true if this generic has exactly two components.
	 * 
	 * @return true if this generic has exactly two components.
	 */
	boolean isReallyRelation();

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> of the base.
	 * 
	 * @param base - the supposed base for the current generic.
	 * 
	 * @return true if this is an <tt>Attribute</tt> of base.
	 */
	boolean isAttributeOf(Generic base);

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> of the base in specified position.
	 * 
	 * @param generic - the supposed base for the current generic.
	 * @param basePos - position of this generic in the array of components of the base.
	 * 
	 * @return true if this generic is an <tt>Attribute</tt> of the base in specified position.
	 */
	boolean isAttributeOf(Generic generic, int basePos);

	/**
	 * Returns the value of this generic.
	 * 
	 * @return the value.
	 */
	<S extends Serializable> S getValue();

	/**
	 * Mark a instance of the <tt>Attribute</tt>.
	 * 
	 * @param attribute - the attribute.
	 * @param targets - the targets.
	 * 
	 * @return A new <tt>Generic</tt> or the existing <tt>Generic</tt>.
	 */
	<T extends Holder> T flag(Holder attribute, Generic... targets);

	/**
	 * Bind this generic to targets via provided relation.
	 * 
	 * @param relation - the relation.
	 * @param targets - the targets.
	 * 
	 * @return A new <tt>Generic</tt> or the existing <tt>Generic</tt>.
	 */
	<T extends Link> T bind(Link relation, Generic... targets);

	/**
	 * Returns the link which instanciates relation provoided in parameters, have the same base
	 * postion in targets and points to all provided targets.
	 * 
	 * @param relation - the relation.
	 * @param basePos - the base position in targets.
	 * @param targets - the optional targets.
	 * 
	 * @return A link or null.
	 * 
	 * @throws IllegalStateException Ambigous request for the Relation.
	 */
	<T extends Link> T getLink(Link relation, int basePos, Generic... targets);

	/**
	 * Returns the link which instanciates relation provided in parameters and points to all
	 * provided targets.
	 * 
	 * @param relation - the relation.
	 * @param targets - the optional targets.
	 * 
	 * @return A link or null.
	 * 
	 * @throws IllegalStateException Ambigous request for the Relation.
	 */
	<T extends Link> T getLink(Link relation, Generic... targets);

	/**
	 * Returns the links.
	 * 
	 * @param relation - the relation.
	 * @param basePos - the base position.
	 * @param targets - the targets.
	 * 
	 * @return the collection of links.
	 * 
	 * @see Snapshot
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, int basePos, Generic... targets);

	/**
	 * Returns the links.
	 * 
	 * @param relation - the relation.
	 * @param targets - the targets.
	 * 
	 * @return the collection of links.
	 * 
	 * @see Snapshot
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, Generic... targets);

	/**
	 * Creates a new link between this generic and targets.<br />
	 * 
	 * Parameters metalink can be of type <tt>Relation</tt> in the most cases. In the case when
	 * there is a default link beetween one Structural and one Concrete, this default link must be
	 * passed as parameter metalink.<br />
	 * 
	 * Exception is thrown is there is another link instanciating the same metalink between the same
	 * components. If the <tt>Singular Constraint</tt> is enabled on the property, then one link
	 * will be created on the targets.
	 * 
	 * @param metalink - <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value - the value of the new link.
	 * @param targets - targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T addLink(Link metalink, Serializable value, Generic... targets);

	/**
	 * Creates a new link between this generic and targets. If the same link is already exists it
	 * will be returned.<br />
	 * 
	 * Parameters metalink can be of type <tt>Relation</tt> in the most cases. In the case when
	 * there is a default link beetween one Structural and one Concrete, this default link must be
	 * passed as parameter metalink.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link
	 * will be created on the targets.
	 * 
	 * @param metalink - <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value - the value of the new link.
	 * @param targets - targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T setLink(Link metalink, Serializable value, Generic... targets);

	/**
	 * Creates a new link between this generic and targets. If the same link is already exists it
	 * will be returned.<br />
	 * 
	 * Parameter metalink can be of type <tt>Relation</tt> in the most cases. In the case when
	 * there is a default link beetween one Structural and one Concrete, this default link must be
	 * passed as parameter metalink.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link
	 * will be created on the targets.
	 * 
	 * @param metalink - <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value - the value of the new link.
	 * @param basePos - the base position.
	 * @param targets - targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T setLink(Link metalink, Serializable value, int basePos, Generic... targets);

	/**
	 * Creates a new holder on this generic. A new holder inherits from metaholder supplied in
	 * parameters.<br />
	 * 
	 * Parameter metaholder is of type <tt>Attribute</tt> the most time but it can also have type
	 * <tt>Holder</tt>.<br />
	 * 
	 * Exception is thrown if this generic has another holder that inherits from the same
	 * metaholder. If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be
	 * created on the targets.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param value - the value of holder.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder metaholder, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned.
	 * A new holder inherits from metaholder supplied in parameters.<br />
	 * 
	 * Parameter metaholder is of type <tt>Attribute</tt> the most time but it can also have type
	 * <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be
	 * created on the targets.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param basePos - the base position.
	 * @param value - the value of holder.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder metaholder, int basePos, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. A new holder inherits from metaholder supplied in
	 * parameters.<br />
	 * 
	 * Parameter metaholder is of type <tt>Attribute</tt> the most time but it can also have type
	 * <tt>Holder</tt>.<br />
	 * 
	 * Exception is thrown if this generic has another holder that inherits from the same
	 * metaholder. If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be
	 * created on the targets.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param value - the value of holder.
	 * @param basePos - the base position.
	 * @param metaLevel - meta level of attribute.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder metaholder, Serializable value, int basePos, int metaLevel, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned.
	 * A new holder inherits from metaholder supplied in parameters.<br />
	 * 
	 * Parameter metaholder is of type <tt>Attribute</tt> the most time but it can also have type
	 * <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be
	 * created on the targets.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param value - the value of holder.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T setHolder(Holder metaholder, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned.
	 * A new holder inherits from metaholder supplied in parameters.<br />
	 * 
	 * Parameter metaholder is of type <tt>Attribute</tt> the most time but it can also have type
	 * <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be
	 * created on the targets.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param basePos - the base position.
	 * @param value - the value of holder.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T setHolder(Holder metaholder, Serializable value, int basePos, Generic... targets);

	/**
	 * Returns all targets of the relation.
	 * 
	 * @param relation - the relation.
	 * 
	 * @return the targets of relation.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation);

	/**
	 * Returns all targets of the relation.
	 * 
	 * @param relation - the relation.
	 * @param basePos - the position of this generic in the array of relation's components.
	 * @param targetPos - the position of the target in the array of relation's components.
	 * 
	 * @return the targets of relation.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation, int basePos, int targetPos);

	/**
	 * Returns all holders that inherit from metaholder supplied in parameters.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param basePos - the base position.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holders that inherit from metaholder.
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder metaholder, int basePos, Generic... targets);

	/**
	 * Returns all holders that inherit from metaholder supplied in parameters.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holders that inherit from metaholder.
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder metaholder, Generic... targets);

	/**
	 * Returns an holder inherited from supplied metaholder.
	 * 
	 * @param metaLevel - meta level.
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param basePos - the base position.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T getHolder(int metaLevel, Holder metaholder, int basePos, Generic... targets);

	/**
	 * Returns an holder inherited from supplied metaholder.
	 * 
	 * @param metaLevel - meta level.
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param targets - the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T getHolder(int metaLevel, Holder metaholder, Generic... targets);

	/**
	 * Returns all values of one holder.
	 * 
	 * @param holder - the holder.
	 * 
	 * @return the values of one holder.
	 * 
	 * @see Snapshot
	 */
	<T extends Serializable> Snapshot<T> getValues(Holder holder);

	/**
	 * Returns the unique value of the holder supplied in parameters.
	 * 
	 * @param holder - the holder.
	 * 
	 * @return the value of holder.
	 */
	<S extends Serializable> S getValue(Holder holder);

	/**
	 * Creates a new value holder (<tt>Holder</tt>) inhereting from metagholder supplied as
	 * parameter.<br />
	 * 
	 * If the same holder is already exists an exception will be thrown. If the <tt>Singular
	 * Constraint</tt> is enabled on the attribute, then one value will be created.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param value - value for new holder.
	 * 
	 * @return the new value holder.
	 */
	<T extends Holder> T addValue(Holder metaholder, Serializable value);

	/**
	 * Creates a new value holder (<tt>Holder</tt>) inhereting from metagholder supplied as
	 * parameter. If the same value holder exists already it will be returned.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the attribute, then one value will be created.
	 * 
	 * @param metaholder - the holder which the new holder inherits from.
	 * @param value - value for new holder.
	 * 
	 * @return the new value holder.
	 */
	<T extends Holder> T setValue(Holder attribute, Serializable value);

	/**
	 * Returns true if this generic directly or indirrectly inherits from meta.
	 * 
	 * @param meta - supposed meta generic which this generic iherits from.
	 * 
	 * @return true if this generic inherits from meta.
	 */
	boolean inheritsFrom(Generic meta);

	/**
	 * Returns true if this generic dirrectly or indirectly inherits from all supplied in parameters
	 * meta generics.
	 * 
	 * @param metas - array of meta generics to test.
	 * 
	 * @return true if this generic inherits from all the given meta generics.
	 */
	boolean inheritsFromAll(Generic... metas);

	/**
	 * Removes this generic from engine.
	 */
	void remove();

	/**
	 * Returns true if this generic was not removed from present cache or from any of it's sub
	 * caches.
	 * 
	 * @return true if this generic still present in any of caches in the current cache stack.
	 */
	boolean isAlive();

	/**
	 * Enable referential integrity for component's position.
	 * 
	 * @param componentPos - the component's position implicated by the constraint.
	 * 
	 * @return this.
	 */
	<T extends Generic> T enableReferentialIntegrity(int componentPos);

	/**
	 * Disable referential integrity for component's position.
	 * 
	 * @param componentPos - the component's position implicated by the constraint.
	 * 
	 * @return this.
	 */
	<T extends Generic> T disableReferentialIntegrity(int componentPos);

	/**
	 * Returns true if the referential integrity is enabled for component's position.
	 * 
	 * @param componentPos - the component's position implicated by the constraint.
	 * 
	 * @return true if the referential integrity is enabled.
	 */
	boolean isReferentialIntegrity(int componentPos);

	/**
	 * Returns the collection of meta generics which this generic directly inherits from.
	 * 
	 * @return the supers.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getSupers();

	/**
	 * Returns the components of this generic.
	 * 
	 * @return the collection of components components.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getComponents();

	/**
	 * Return the number of components.
	 * 
	 * @return the number of components.
	 */
	int getComponentsSize();

	/**
	 * Returns the base position of an attribute.
	 * 
	 * @param attribute - the attribute.
	 * 
	 * @return the base position of the attribute.
	 */
	int getBasePos(Holder attribute);

	/**
	 * Returns the number of supers.
	 * 
	 * @return the number of supers.
	 */
	int getSupersSize();

	/**
	 * Creates a new anonymous instance.
	 * 
	 * @param components - the components.
	 * 
	 * @return the new anonymous instance.
	 */
	<T extends Generic> T newAnonymousInstance(Generic... components);

	/**
	 * Create a new instance or get the instance if it already exists.
	 * 
	 * @param value - the value.
	 * @param components - the components.
	 * 
	 * @return the new instance.
	 */
	<T extends Generic> T newInstance(Serializable value, Generic... components);

	/**
	 * Return the meta.
	 * 
	 * @return the meta.
	 */
	<T extends Generic> T getMeta();

	/**
	 * Returns inheritings.
	 *
	 * @return The inheritings Generic.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInheritings();

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Returns the composites Generic.
	 * 
	 * @see Snapshot
	 * @return The composites Generic.
	 */
	<T extends Generic> Snapshot<T> getComposites();

	/**
	 * Returns true if the Generic is structural.
	 * 
	 * @return True if the Generic is structural.
	 */
	boolean isStructural();

	/**
	 * Returns true if the Generic is concrete.
	 * 
	 * @return True if the Generic is concrete.
	 */
	boolean isConcrete();

	/**
	 * Returns true if the Generic is meta.
	 * 
	 * @return True if the Generic is meta.
	 */
	boolean isMeta();

	/**
	 * Returns true if the Generic is Map Provider.
	 * 
	 * @return true if the Generic is Map Provider.
	 */
	boolean isMapProvider();

	/**
	 * Returns true if the Generic is tree.
	 * 
	 * @return True if the Generic is tree.
	 */
	boolean isTree();

	/**
	 * Returns true if the Generic is root.
	 * 
	 * @return True if the Generic is root.
	 */
	boolean isRoot();

	/**
	 * Returns true if the Generic is removable.
	 * 
	 * 
	 * @return True if the Generic is removable.
	 */
	boolean isRemovable();

	/**
	 * Log with slf4j.<br/>
	 * Call the info() method.
	 */
	void log();

	/**
	 * Returns all available information except linkage information.
	 * 
	 * @return all available information except linkage information.
	 */
	String info();

	void clearAll(Holder attribute, int metaLevel, Generic... targets);

	void clearAll(Holder attribute, int metaLevel, int basePos, Generic... targets);

	void cancelAll(Holder attribute, int metaLevel, Generic... targets);

	void cancelAll(Holder attribute, int basePos, int metaLevel, Generic... targets);

	<T extends Generic> T cancel(Holder attribute, Generic... targets);

	<T extends Generic> T cancel(Holder attribute, int basePos, Generic... targets);

	<T extends MapProvider> Map<Serializable, Serializable> getMap(Class<T> mapClass);

	Map<Serializable, Serializable> getPropertiesMap();

	ExtendedMap<Serializable, Serializable> getConstraintsMap();

	ExtendedMap<Serializable, Serializable> getSystemPropertiesMap();

	interface ExtendedMap<K, V> extends Map<K, V> {
		Holder getValueHolder(Serializable key);
	}

	<T extends Generic> T addComponent(int pos, Generic newComponent);

	<T extends Generic> T removeComponent(int pos, Generic newComponent);

	<T extends Generic> T addSuper(Generic newSuper);

	<T extends Generic> T removeSuper(int pos);

	<T extends Generic> T updateValue(Serializable value);

	<T extends Generic> Snapshot<T> getOtherTargets(Holder holder);

	void removeHolder(Holder holder);

	<T extends Holder> Snapshot<T> getHolders(Holder attribute, boolean readPhantoms, Generic... targets);

	<T extends Holder> Snapshot<T> getHolders(Holder attribute, int basePos, boolean readPhantoms, Generic... targets);

	void removePhantoms(Attribute attribute);

	boolean fastValueEquals(Generic generic);
}
