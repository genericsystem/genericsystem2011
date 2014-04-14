package org.genericsystem.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.generic.Relation;

/**
 * <h1>Definition</h1>
 * <p>
 * Almost everything in <tt>Generic System</tt> is <tt>Generic</tt>. Every entity held by <tt>Engine</tt> implements this interface.
 * </p>
 * 
 * <p>
 * <tt>Generic System</tt> is a graph. Interface <tt>Generic</tt> represents the node of this graph. This graph has entity <tt>Engine</tt> as it's root. Every new entity must to be plugged on the graph formed by descendants of <tt>Engine</tt>.
 * </p>
 * 
 * <h1>Meta-levels</h1>
 * <p>
 * In <tt>Generic System</tt> exist tree meta-levels of generics:
 * <dl>
 * <dt>Meta
 * <dt>
 * <dd>This level define the meta-model. Here we found generics necessary for the function of the system.</dd>
 * <dt>Structurals
 * <dt>
 * <dd>On this level we find user-defined data-models: <tt>Types</tt>, <tt>Attributes</tt> and <tt>Relations</tt></dd>
 * <dt>Concretes
 * <dt>
 * <dd>Data level. Level of the objects that inherit from user-defined classes. This level defines business-data. Managed by <tt>Generic System</tt>.</dd>
 * </dl>
 * </p>
 * 
 * <h1>Types of nodes</h1>
 * <p>
 * <tt>Generic System</tt> is the graph. It is possible to access any related node of any <tt>Generic</tt>. <tt>Generic</tt> has four categories of related nodes:
 * <dl>
 * <dt>Supers</dt>
 * <dd>Nodes that this <tt>Generic</tt> inherits from.</dd>
 * <dt>Inheretings</dt>
 * <dd>Nodes which inherit from this <tt>Generic</tt>.</dd>
 * <dt>Components</dt>
 * <dd>Nodes that contained by this <tt>Generic</tt>.</dd>
 * <dt>Composites</dt>
 * <dd>Nodes that contains this <tt>Generic</tt>.</dd>
 * </dl>
 * </p>
 * 
 * <p>
 * This interface is a part of <tt>Generic System Core</tt>.
 * </p>
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
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
	 * Returns <tt>true</tt> if this generic is direct instance of the meta passed as parameter.
	 * 
	 * @param meta
	 *            the meta.
	 * 
	 * @return <tt>true</tt> if this is the direct instance of meta.
	 */
	boolean isInstanceOf(Generic meta);

	/**
	 * Returns instantiation level of the generic. Generic System allows to create infinite meta levels by inheritance of concretes. Three meta levels actually important to know in Generic System:
	 * <dl>
	 * <dt>0</dt>
	 * <dd>- Level of Meta-Objects (Engine, MetaAttribute and MetaRelation);</dd>
	 * <dt>1</dt>
	 * <dd>- Level of Structurals (Types, Attributes, Relations);</dd>
	 * <dt>2</dt>
	 * <dd>- Level of Concretes ("Instances", Holders, Links).</dd>
	 * </dl>
	 * 
	 * @return the instantiation level.
	 */
	int getMetaLevel();

	/**
	 * Returns true if this generic is a <tt>Type</tt>.
	 * 
	 * @return true if this is a <tt>Type</tt>.
	 */
	boolean isType();

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> or <tt>Relation</tt>.
	 * 
	 * @return true if the generic is an <tt>Attribute</tt> or <tt>Relation</tt>.
	 */
	boolean isAttribute();

	/**
	 * Returns true if this generic has at least two components.
	 * 
	 * @return true if this generic has at least two components.
	 */
	boolean isRelation();

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> of the generic.
	 * 
	 * @param generic
	 *            the supposed "type" for the current generic.
	 * 
	 * @return true if this is an <tt>Attribute</tt> of generic.
	 */
	boolean isAttributeOf(Generic generic);

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> of the generic in specified position.
	 * 
	 * @param generic
	 *            the supposed "type" for the current generic.
	 * @param basePos
	 *            position of this generic in the array of components of the "type".
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
	 * @param attribute
	 *            the attribute.
	 * @param targets
	 *            the targets.
	 * 
	 * @return A new <tt>Generic</tt> or the existing <tt>Generic</tt>.
	 */
	<T extends Holder> T flag(Holder attribute, Generic... targets);

	/**
	 * Bind this generic to targets via provided relation.
	 * 
	 * @param relation
	 *            the relation.
	 * @param targets
	 *            the targets.
	 * 
	 * @return A new <tt>Generic</tt> or the existing <tt>Generic</tt>.
	 */
	<T extends Link> T bind(Link relation, Generic... targets);

	/**
	 * Returns the link which instantiates relation provided in parameters, have the same base position and points to all provided targets.
	 * 
	 * @param relation
	 *            the relation.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targets
	 *            the optional targets.
	 * 
	 * @return A link or null.
	 * 
	 * @throws RollbackException
	 *             RollbackException is throw if the request is ambiguous. She return more one result.
	 */
	<T extends Link> T getLink(Link relation, int basePos, Generic... targets);

	/**
	 * Returns the link which instantiates relation provided in parameters and points to all provided targets.
	 * 
	 * @param relation
	 *            the relation.
	 * @param targets
	 *            the optional targets.
	 * 
	 * @return A link or null.
	 * 
	 * @throws RollbackException
	 *             RollbackException is throw if the request is ambiguous. She return more one result.
	 */
	<T extends Link> T getLink(Link relation, Generic... targets);

	/**
	 * Returns the links which instantiates relation provided in parameters and points to all provided targets.
	 * 
	 * @param relation
	 *            the relation.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targets
	 *            the optional targets.
	 * 
	 * @return the Snapshot of links.
	 * @see Snapshot
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, int basePos, Generic... targets);

	/**
	 * Returns the links which instantiates relation provided in parameters and points to all provided targets.
	 * 
	 * @param relation
	 *            the relation.
	 * @param targets
	 *            the targets.
	 * 
	 * @return the collection of links.
	 * 
	 * @see Snapshot
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, Generic... targets);

	/**
	 * Creates a new link between this generic and targets.<br />
	 * 
	 * Parameters relation can be of type <tt>Relation</tt> in the most cases. In the case when there is a default link between one Structural and one Concrete, this default link must be passed as parameter relation.<br />
	 * 
	 * Exception is thrown is there is another link instantiating the same relation between the same components. <br/>
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the relation, then one link will be created on the targets.
	 * 
	 * @param relation
	 *            <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value
	 *            the value of the new link.
	 * @param targets
	 *            targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T addLink(Link relation, Serializable value, Generic... targets);

	/**
	 * Creates a new link between this generic and targets. If the same link already exists an exception is thrown.<br />
	 * 
	 * Parameters relation can be of type <tt>Relation</tt> in the most cases. In the case when there is a default link beetween one Structural and one Concrete, this default link must be passed as parameter relation.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param relation
	 *            <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value
	 *            the value of the new link.
	 * @param targets
	 *            targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T setLink(Link relation, Serializable value, Generic... targets);

	/**
	 * Creates a new link between this generic and targets. If the same link already exists it will be returned.<br />
	 * 
	 * Parameter relation can be of type <tt>Relation</tt> in the most cases. In the case when there is a default link beetween one Structural and one Concrete, this default link must be passed as parameter relation.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param relation
	 *            <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value
	 *            the value of the new link.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targets
	 *            targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T setLink(Link relation, Serializable value, int basePos, Generic... targets);

	/**
	 * Creates a new link between this generic and targets. If the same link is already exists it will be returned.<br />
	 * 
	 * Parameter relation can be of type <tt>Relation</tt> in the most cases. In the case when there is a default link beetween one Structural and one Concrete, this default link must be passed as parameter relation.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param relation
	 *            <tt>Relation</tt> or <tt>Link</tt> a new link inherits from.
	 * @param value
	 *            the value of the new link.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param metaLevel
	 *            meta level.
	 * @param targets
	 *            targets of the new link.
	 * 
	 * @return the link.
	 */
	<T extends Link> T setLink(Link relation, Serializable value, int basePos, int metaLevel, Generic... targets);

	/**
	 * Creates a new holder on this generic. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * Exception is thrown if this generic has another holder that inherits from the same attribute.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value of holder.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder attribute, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * Exception is thrown if this generic has another holder that inherits from the same attribute.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param value
	 *            the value of holder.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder attribute, int basePos, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * Exception is thrown if this generic has another holder that inherits from the same attribute.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value of holder.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param metaLevel
	 *            meta level of attribute.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T addHolder(Holder attribute, Serializable value, int basePos, int metaLevel, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value of holder.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T setHolder(Holder attribute, Serializable value, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value of holder.
	 * @param metaLevel
	 *            meta level.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T setHolder(Holder attribute, Serializable value, int metaLevel, Generic... targets);

	/**
	 * Creates a new holder on this generic. If the same holder exists already it will be returned. A new holder inherits from attribute supplied in parameters.<br />
	 * 
	 * Parameter attribute is of type <tt>Attribute</tt> the most time but it can also have type <tt>Holder</tt>.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the property, then one link will be created on the targets.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param metaLevel
	 *            meta level.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param value
	 *            the value of holder.
	 * @param targets
	 *            the optinal targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T setHolder(Holder attribute, Serializable value, int metaLevel, int basePos, Generic... targets);

	/**
	 * Returns all targets of the relation.
	 * 
	 * @param relation
	 *            the relation.
	 * 
	 * @return the targets of relation.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation);

	/**
	 * Returns all targets of the relation.
	 * 
	 * @param relation
	 *            the relation.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targetPos
	 *            the position of the target in the array of relation's components.
	 * 
	 * @return the targets of relation.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation, int basePos, int targetPos);

	/**
	 * Returns all holders that inherit from attribute supplied in parameters.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targets
	 *            the optional targets for link.
	 * 
	 * @return the holders that inherit from attribute.
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder attribute, int basePos, Generic... targets);

	/**
	 * Returns all holders that inherit from attribute supplied in parameters.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param targets
	 *            the optional targets for link.
	 * 
	 * @return the holders that inherit from attribute.
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder attribute, Generic... targets);

	/**
	 * Returns an holder inherited from supplied attribute.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param targets
	 *            the optional targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T getHolder(Holder attribute, Generic... targets);

	/**
	 * Returns an holder inherited from supplied attribute.
	 * 
	 * @param metaLevel
	 *            meta level.
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param basePos
	 *            the position of the based generic in relation.
	 * @param targets
	 *            the optional targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T getHolder(int metaLevel, Holder attribute, int basePos, Generic... targets);

	/**
	 * Returns an holder inherited from supplied attribute.
	 * 
	 * @param metaLevel
	 *            meta level.
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param targets
	 *            the optional targets for link.
	 * 
	 * @return the holder.
	 */
	<T extends Holder> T getHolder(int metaLevel, Holder attribute, Generic... targets);

	/**
	 * Returns all values of one holder.
	 * 
	 * @param holder
	 *            the holder.
	 * 
	 * @return the values of one holder.
	 * 
	 * @see Snapshot
	 */
	<T extends Serializable> Snapshot<T> getValues(Holder holder);

	/**
	 * Returns the unique value of the holder supplied in parameters.
	 * 
	 * @param holder
	 *            the holder.
	 * 
	 * @return the value of holder.
	 */
	<S extends Serializable> S getValue(Holder holder);

	/**
	 * Creates a new value holder (<tt>Holder</tt>) inheriting from attribute supplied as parameter.<br />
	 * 
	 * If the same holder is already exists an exception will be thrown. <br/>
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the attribute, then one value will be created.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value for new holder.
	 * 
	 * @return the new value holder.
	 */
	<T extends Holder> T addValue(Holder attribute, Serializable value);

	/**
	 * Creates a new value holder (<tt>Holder</tt>) inheriting from attribute supplied as parameter. If the same value holder exists already it will be returned.<br />
	 * 
	 * If the <tt>Singular Constraint</tt> is enabled on the attribute, then one value will be created.
	 * 
	 * @param attribute
	 *            the holder which the new holder inherits from.
	 * @param value
	 *            the value for new holder.
	 * 
	 * @return the new value holder.
	 */
	<T extends Holder> T setValue(Holder attribute, Serializable value);

	/**
	 * Returns true if this directly or indirectly inherits from generic.
	 * 
	 * @param generics
	 *            supposed generic which this generic inherits from.
	 * 
	 * @return true if this generic inherits from meta.
	 */
	boolean inheritsFrom(Generic generic);

	/**
	 * Returns true if this directly or indirectly inherits from all supplied in parameters generics.
	 * 
	 * @param generics
	 *            array of generics to test.
	 * 
	 * @return true if this generic inherits from all the given generics.
	 */
	boolean inheritsFromAll(Generic... generics);

	/**
	 * Removes this generic from engine.
	 */
	void remove();

	/**
	 * Remove the Generic.
	 * 
	 * @param removeStrategy
	 *            strategy of remove
	 * @see RemoveStrategy
	 */
	void remove(RemoveStrategy removeStrategy);

	/**
	 * Returns true if this generic was not removed from present cache or from any of it's sub caches.
	 * 
	 * @return true if this generic still present in any of caches in the current cache stack.
	 */
	boolean isAlive();

	/**
	 * Enable referential integrity for component's base position.
	 * 
	 * 
	 * @return this.
	 */
	<T extends Generic> T enableReferentialIntegrity();

	/**
	 * Disable referential integrity for component's base position.
	 * 
	 * 
	 * @return this.
	 */
	<T extends Generic> T disableReferentialIntegrity();

	/**
	 * Enable referential integrity for component's position.
	 * 
	 * @param componentPos
	 *            the component's position implicated by the constraint.
	 * 
	 * @return this.
	 */
	<T extends Generic> T enableReferentialIntegrity(int componentPos);

	/**
	 * Disable referential integrity for component's position.
	 * 
	 * @param componentPos
	 *            the component's position implicated by the constraint.
	 * 
	 * @return this.
	 */
	<T extends Generic> T disableReferentialIntegrity(int componentPos);

	/**
	 * Returns true if the referential integrity is enabled for component's base position.
	 * 
	 * 
	 * @return true if the referential integrity is enabled on base position.
	 */
	boolean isReferentialIntegrity();

	/**
	 * Returns true if the referential integrity is enabled for component's position.
	 * 
	 * @param componentPos
	 *            the component's position implicated by the constraint.
	 * 
	 * @return true if the referential integrity is enabled.
	 */
	boolean isReferentialIntegrity(int componentPos);

	/**
	 * Returns an unmodifiable list of generics which this generic directly inherits from.
	 * 
	 * @return the list of supers.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> List<T> getSupers();

	/**
	 * Returns an unmodifiable list of components of this generic.
	 * 
	 * @return the list of components.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> List<T> getComponents();

	/**
	 * Returns the base position of an attribute.
	 * 
	 * @param attribute
	 *            the attribute.
	 * 
	 * @return the base position of the attribute.
	 */
	int getBasePos(Holder attribute);

	/**
	 * Return the meta.
	 * 
	 * @return the meta.
	 */
	<T extends Generic> T getMeta();

	/**
	 * Returns inheriting.
	 * 
	 * @return the inheriting Generic.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInheritings();

	/**
	 * Returns the composites.
	 * 
	 * @return the collection of composites.
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getComposites();

	/**
	 * Returns true if this generic is a structural.
	 * 
	 * @return true if this generic is a structural.
	 */
	boolean isStructural();

	/**
	 * Returns true if this generic is a concrete.
	 * 
	 * @return true if this generic is a concrete.
	 */
	boolean isConcrete();

	/**
	 * Returns true if this generic is meta.
	 * 
	 * @return true if this generic is meta.
	 */
	boolean isMeta();

	/**
	 * Returns true if this generic is a Map Provider.
	 * 
	 * @return true if this generic is a Map Provider.
	 */
	boolean isMapProvider();

	/**
	 * Returns true if this generic is a tree.
	 * 
	 * @return true if this generic is a tree.
	 */
	boolean isTree();

	/**
	 * Returns true if the generic is a root.
	 * 
	 * @return true if the generic is a root.
	 */
	boolean isRoot();

	/**
	 * Returns true if this generic is removable.
	 * 
	 * @return True if this generic is removable.
	 */
	boolean isRemovable();

	/**
	 * Log the state of this generic with SLF4J.
	 */
	void log();

	/**
	 * Returns all available information except information about links.
	 * 
	 * @return all available information except information about links.
	 */
	String info();

	/**
	 * Abandons concrete values of the holder setting it to null.
	 * 
	 * @param holder
	 *            the holder.
	 */
	void cancel(Holder holder);

	/**
	 * Abandons concrete values of the holder setting it to null.
	 * 
	 * @param holder
	 *            the holder.
	 * @param targets
	 *            the optional targets for link.
	 */
	void cancelAll(Holder holder, Generic... targets);

	/**
	 * Removes holder from the graph.
	 * 
	 * @param holder
	 *            the holder.
	 */
	void clear(Holder holder);

	/**
	 * Removes holder from the graph.
	 * 
	 * @param holder
	 *            the holder.
	 * @param targets
	 *            the optional targets for relation.
	 */
	void clearAll(Holder holder, Generic... targets);

	/**
	 * Returns the map associated with this generic. Map is found by class of Map Provider.
	 * 
	 * @param mapClass
	 *            the class of Map Provider.
	 * 
	 * @return the map object.
	 */
	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getMap(Class<? extends MapProvider> mapClass);

	/**
	 * Returns the map of properties associated with this generic.
	 * 
	 * @return the map with properties.
	 */
	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getPropertiesMap();

	/**
	 * Adds a new component into defined position in array of generic's components.
	 * 
	 * @param component
	 *            the component to insert.
	 * @param pos
	 *            the position of component in array.
	 * 
	 * @return this generic.
	 */
	<T extends Generic> T addComponent(Generic component, int pos);

	/**
	 * Removes given component in defined position.
	 * 
	 * @param component
	 *            the component to remove.
	 * @param pos
	 *            the position of component in array.
	 * 
	 * @return this generic.
	 */
	<T extends Generic> T removeComponent(Generic component, int pos);

	/**
	 * Adds a new super (generic which this inherits from).
	 * 
	 * @param newSuper
	 *            the new super generic.
	 * 
	 * @return this generic.
	 */
	<T extends Generic> T addSuper(Generic newSuper);

	/**
	 * Removes the super generic in defined position.
	 * 
	 * @param pos
	 *            position of super in array of supers.
	 * 
	 * @return this generic.
	 */
	<T extends Generic> T removeSuper(int pos);

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value for this generic.
	 * 
	 * @return this generic.
	 */
	<T extends Generic> T setValue(Serializable value);

	/**
	 * Returns the other component of holder (not this generic and it's inheriting).
	 * 
	 * @param holder
	 *            the holder.
	 * 
	 * @return <tt>Snapshot</tt> of components.
	 */
	<T extends Generic> Snapshot<T> getOtherTargets(Holder holder);

	/**
	 * Returns true if values of this generic and generic supplied in parameters are equal.
	 * 
	 * @param generic
	 *            the generic to compare.
	 * 
	 * @return true if values of this generic and generic supplied in parameters are equal.
	 */
	boolean fastValueEquals(Generic generic);

	/**
	 * Returns true if this generic is used by the system. System generics are not removable.
	 * 
	 * @return true if class of this generic is SystemGeneric annotated.
	 */
	boolean isSystem();

}
