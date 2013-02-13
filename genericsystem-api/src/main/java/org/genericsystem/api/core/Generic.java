package org.genericsystem.api.core;

import java.io.Serializable;

import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;

/**
 * Generic is main interface of each node of the internal graph.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Generic extends Comparable<Generic> {

	/**
	 * Returns the root of the internal graph to which this Generic belongs.
	 * 
	 * @return The Engine.
	 */
	Engine getEngine();

	/**
	 * Returns true if this Generic is the root of the internal graph.
	 * 
	 * @return True if this Generic is the root.
	 */
	boolean isEngine();

	/**
	 * Returns true if this Generic is an instance of the specified Generic.
	 * 
	 * @param Generic
	 *            The checked type.
	 * @return True if the Generic is a instance of the type checked.
	 */
	boolean isInstanceOf(Generic generic);

	/**
	 * Returns instantiation level.
	 * 
	 * @return The instantiation level.
	 */
	int getMetaLevel();

	/**
	 * Returns true if this Generic is a Type.
	 * 
	 * @return True if the Generic is a Type.
	 */
	boolean isType();

	/**
	 * Returns true if this Generic is an Attribute.
	 * 
	 * @return True if the Generic is an Attribute.
	 */
	boolean isAttribute();

	/**
	 * Returns true if this Generic is an Really Attribute (no relation).
	 * 
	 * @return True if the Generic is an Really Attribute.
	 */
	boolean isReallyAttribute();

	/**
	 * Returns true if this Generic is an Attribute for the checked Generic.
	 * 
	 * @param Generic
	 *            The checked Generic.
	 * 
	 * @return True if the Generic is an Attribute.
	 */
	boolean isAttributeOf(Generic generic);

	/**
	 * Returns true if this Generic is an Attribute for the checked Generic and the component position.
	 * 
	 * @param Generic
	 *            The checked Generic.
	 * @param componentPos
	 *            The component position.
	 * 
	 * @return True if the Generic is an Attribute.
	 */
	boolean isAttributeOf(Generic generic, int componentPos);

	/**
	 * Returns true if this Generic is an relation.
	 * 
	 * @return True if the Generic is an Relation.
	 */
	boolean isRelation();

	/**
	 * Returns the value of this Generic.
	 * 
	 * @return The value.
	 */
	<S extends Serializable> S getValue();

	/**
	 * Mark a instance of the Attribute.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param attribute
	 *            The attribute.
	 */
	void flag(Cache cache, Attribute attribute);

	/**
	 * Bind this with the targets.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param relation
	 *            The Relation.
	 * @param targets
	 *            The targets.
	 * @return A new Generic or the existing Generic.
	 */
	<T extends Link> T bind(Cache cache, Link relation, Generic... targets);

	/**
	 * Bind this with the targets.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param relation
	 *            The Relation.
	 * @param basePos
	 *            The base component position.
	 * @param targets
	 *            The targets.
	 * @return A new Generic or the existing Generic.
	 */
	<T extends Link> T bind(Cache cache, Link relation, int basePos, Generic... targets);

	/**
	 * Returns the Link of the Relation for the components.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The Relation.
	 * @param targets
	 *            The optional targets.
	 * @return A Link.
	 * @throws IllegalStateException
	 *             Ambigous request for the Relation.
	 */
	<T extends Link> T getLink(Context context, Relation relation, Generic... targets);

	/**
	 * Returns the Link of the Relation for the components.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The Relation.
	 * @param basePos
	 *            The base component position.
	 * @param targets
	 *            The optional targets.
	 * @return A Link.
	 * @throws IllegalStateException
	 *             Ambigous request for the Relation.
	 */
	<T extends Link> T getLink(Context context, Relation relation, int basePos, Generic... targets);

	/**
	 * Returns the Link.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The Relation.
	 * @param targets
	 *            The targets.
	 * @see Snapshot
	 * @return The Link.
	 */
	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation, Generic... targets);

	/**
	 * Returns the Link.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The Relation.
	 * @param basePos
	 *            The base component position.
	 * @param targets
	 *            The targets.
	 * @see Snapshot
	 * @return The Link.
	 */
	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation, int basePos, Generic... targets);

	/**
	 * Modify or create a Link. <br/>
	 * If the Singular constraint is enabled on the property, then one link will be created on the targets.<br/>
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param property
	 *            The property.
	 * @param value
	 *            The value Link.
	 * @param targets
	 *            The optional targets.
	 * @return The Link.
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value, Generic... targets);

	/**
	 * Modify or create a Link. <br/>
	 * If the Singular constraint is enabled on the property, then one link will be created on the targets.<br/>
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param property
	 *            The property.
	 * @param value
	 *            The value Link.
	 * @param basePos
	 *            The base component position.
	 * @param targets
	 *            The optional targets.
	 * @return The Link.
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value, int basePos, Generic... targets);

	/**
	 * Returns the targets of the Relation.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The relation.
	 * @see Snapshot
	 * @return The targets.
	 */
	<T extends Generic> Snapshot<T> getTargets(Context context, Relation relation);

	/**
	 * Returns the targets of the Relation.
	 * 
	 * @param context
	 *            The reference context.
	 * @param relation
	 *            The relation.
	 * @param basePos
	 *            The base component position.
	 * @param targetPos
	 *            The target component position.
	 * 
	 * @see Snapshot
	 * @return The targets.
	 */
	<T extends Generic> Snapshot<T> getTargets(Context context, Relation relation, int basePos, int targetPos);

	/**
	 * Returns the values holders.
	 * 
	 * @param context
	 *            The reference context.
	 * @param attribute
	 *            The attribute.
	 * @see Snapshot
	 * @return The values holders.
	 */
	<T extends Holder> Snapshot<T> getHolders(Context context, T attribute);

	/**
	 * Returns the Holder of value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param attribute
	 *            The attribute.
	 * @return The Holder.
	 */
	<T extends Holder> T getHolder(Context context, Attribute attribute);

	/**
	 * Returns the values.
	 * 
	 * @param context
	 *            The reference context.
	 * @param attribute
	 *            The attribute.
	 * @see Snapshot
	 * @return The values.
	 */
	<T extends Serializable> Snapshot<T> getValues(final Context context, final Attribute attribute);

	/**
	 * Returns the value of the attribute.
	 * 
	 * @param attribute
	 *            The attribute.
	 * @return The value.
	 */
	<S extends Serializable> S getValue(Context context, Attribute attribute);

	/**
	 * Modify or create a Holder. <br/>
	 * If the Singular constraint is enabled on the attribute, then one value will be created.<br/>
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param attribute
	 *            The attribute.
	 * @param value
	 *            The name value.
	 * @return The value holder.
	 */
	<T extends Holder> T setValue(Cache cache, Attribute attribute, Serializable value);

	/**
	 * Returns true if the Generic inherits from the given Generic.
	 * 
	 * @param Generic
	 *            The checked Generic.
	 * @return True if the Generic inherits from the given Generic.
	 */
	boolean inheritsFrom(Generic generic);

	/**
	 * Returns true if the Generic inherits from all the given Generic.
	 * 
	 * @param generics
	 *            The given Generic.
	 * @return True if the Generic inherits from all the given Generic.
	 */
	boolean inheritsFromAll(Generic... generics);

	/**
	 * Remove the Generic.
	 * 
	 * @param cache
	 *            The reference Cache.
	 */
	void remove(Cache cache);

	/**
	 * Returns true if the Generic is alive
	 * 
	 * @param context
	 *            The reference Context.
	 * @return True if the Generic is alive.
	 */
	boolean isAlive(Context context);

	/**
	 * Enable system property.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @return This.
	 */
	<T extends Generic> T enableSystemProperty(Cache cache, Class<?> genericInCacheClass);

	/**
	 * Enable system property for component position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @param componentPos
	 *            The component position.
	 * @return This.
	 */
	<T extends Generic> T enableSystemProperty(Cache cache, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Disable system property.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @return This.
	 */
	<T extends Generic> T disableSystemProperty(Cache cache, Class<?> genericInCacheClass);

	/**
	 * Disable system property for component position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @param componentPos
	 *            The component position.
	 * @return This.
	 */
	<T extends Generic> T disableSystemProperty(Cache cache, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Returns true if the system property is enabled.
	 * 
	 * @param context
	 *            The reference context.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @return True if the system property is enabled.
	 */
	boolean isSystemPropertyEnabled(Context context, Class<?> genericInCacheClass);

	/**
	 * Returns true if the system property is enabled for component position.
	 * 
	 * @param context
	 *            The reference context.
	 * @param genericInCacheClass
	 *            Classe which defines the Generic in Cache.
	 * @param componentPos
	 *            The component position.
	 * @return True if the system property is enabled.
	 */
	boolean isSystemPropertyEnabled(Context context, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Enable referential integrity for component position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position.
	 * @return This.
	 */
	<T extends Generic> T enableReferentialIntegrity(Cache cache, int componentPos);

	/**
	 * Disable referential integrity for component position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position.
	 * 
	 * @return This.
	 */
	<T extends Generic> T disableReferentialIntegrity(Cache cache, int componentPos);

	/**
	 * Returns true if the referential integrity is enabled for component position.
	 * 
	 * @param context
	 *            The reference context.
	 * @param componentPos
	 *            The component position.
	 * @return True if the referential integrity is enabled.
	 */
	boolean isReferentialIntegrity(Context context, int componentPos);

	/**
	 * Returns the implicit.
	 * 
	 * @return The implicit.
	 */
	<T extends Generic> T getImplicit();

	/**
	 * Returns the supers of the Generic.
	 * 
	 * @see Snapshot
	 * @return The supers.
	 */
	<T extends Generic> Snapshot<T> getSupers();

	/**
	 * Returns the components of the Generic.
	 * 
	 * @see Snapshot
	 * @return The components.
	 */
	<T extends Generic> Snapshot<T> getComponents();

	/**
	 * Returns the size of components.
	 * 
	 * @return The size of components.
	 */
	int getComponentsSize();

	/**
	 * Returns the size of supers.
	 * 
	 * @return The size of supers.
	 */
	int getSupersSize();

	/**
	 * Create a new anonymous instance.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param components
	 *            The components.
	 * @return The new Generic.
	 */
	<T extends Generic> T newAnonymousInstance(Cache cache, Generic... components);

	/**
	 * Create a new instance or get the instance if it already exists.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The value.
	 * @param components
	 *            The components.
	 * @return The new Generic.
	 */
	<T extends Generic> T newInstance(Cache cache, Serializable value, Generic... components);

	/**
	 * Return the meta.
	 * 
	 * @return The meta.
	 */
	<T extends Generic> T getMeta();

	/**
	 * Returns the inheritings Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @see Snapshot
	 * @return The inheritings Generic.
	 */
	<T extends Generic> Snapshot<T> getInheritings(Context context);

	/**
	 * Returns the composites Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @see Snapshot
	 * @return The composites Generic.
	 */
	<T extends Generic> Snapshot<T> getComposites(Context context);

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
	 * Returns true if the Generic is tree.
	 * 
	 * @return True if the Generic is tree.
	 */
	boolean isTree();

	void log();

	String info();

}
