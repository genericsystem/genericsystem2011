package org.genericsystem.api.core;

import java.io.Serializable;

import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;

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
	 * Returns true if this Generic is an Attribute for the checked Generic and
	 * the component position.
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

	// /**
	// * Add a value for the attribute
	// *
	// * @param attribute
	// * the attribute
	// * @param value
	// * the value
	// * @return the object that represents the value
	// * @see Value
	// */
	// <T extends Value> T addValue(Cache cache, Value attribute, Serializable
	// value);

	/**
	 * Mark a instance of the Attribute.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param attribute
	 */
	void flag(Cache cache, Value attribute);

	// <T extends Link> T addLink(Cache cache, Link relation, Serializable
	// value, int basePos, Generic... targets);

	// /**
	// * Add a link for the relation
	// *
	// * @param relation
	// * the relation
	// * @param value
	// * the value of link
	// * @param generic
	// * the generic links targets
	// * @return the link
	// * @see Link
	// */
	// <T extends Link> T addLink(Cache cache, Link relation, Serializable
	// value, Generic... generic);

	/**
	 * 
	 * @param cache
	 * @param relation
	 * @param Generic
	 * @return
	 */
	<T extends Link> T bind(Cache cache, Link relation, Generic... generic);

	<T extends Link> T bind(Cache cache, Link relation, int basePos,
			Generic... targets);

	/**
	 * 
	 * @param relation
	 *            the relation
	 * @param targets
	 *            the optional targets
	 */
	<T extends Link> T getLink(Context context, Relation relation,
			Generic... targets);

	/**
	 * 
	 * @param relation
	 *            the relation
	 * @param targets
	 *            the optional targets
	 */
	<T extends Link> T getLink(Context context, Relation relation, int basePos,
			Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * 
	 * @param value
	 *            the value
	 * @param targets
	 *            the optional targets
	 * @return the link
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value,
			Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * 
	 * @param value
	 *            the value
	 * @param targets
	 *            the optional targets
	 * @return the link
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value,
			int basePos, Generic... targets);

	/**
	 * Returns true if the Generic inherits from the given Generic
	 * 
	 * @param Generic
	 *            the checked Generic
	 * @return true if the Generic inherits from the given Generic
	 */
	boolean inheritsFrom(Generic generic);

	/**
	 * Returns true if the Generic inherits from all the given generics
	 * 
	 * @param generics
	 *            the given generics
	 * @return true if the Generic inherits from all the given generics
	 */
	boolean inheritsFromAll(Generic... generics);

	/**
	 * Remove the Generic
	 */
	void remove(Cache cache);

	/**
	 * Returns true if the Generic is alive
	 * 
	 * @return true if the Generic is alive
	 */
	boolean isAlive(Context context);

	/**
	 * Enable system property
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @return this
	 */
	<T extends Generic> T enableSystemProperty(Cache cache,
			Class<?> genericInCacheClass);

	/**
	 * Enable system property for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T enableSystemProperty(Cache cache,
			Class<?> genericInCacheClass, int componentPos);

	/**
	 * Disable system property
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @return this
	 */
	<T extends Generic> T disableSystemProperty(Cache cache,
			Class<?> genericInCacheClass);

	/**
	 * Disable system property for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T disableSystemProperty(Cache cache,
			Class<?> genericInCacheClass, int componentPos);

	/**
	 * Returns true if the system property is enabled
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @return true if the system property is enabled
	 */
	boolean isSystemPropertyEnabled(Context context,
			Class<?> genericInCacheClass);

	/**
	 * Returns true if the system property is enabled for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the Generic in cache
	 * @param componentPos
	 *            the component position
	 * @return true if the system property is enabled
	 */
	boolean isSystemPropertyEnabled(Context context,
			Class<?> genericInCacheClass, int componentPos);

	/**
	 * Enable referential integrity for component position
	 * 
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T enableReferentialIntegrity(Cache cache,
			int componentPos);

	/**
	 * Disable referential integrity for component position
	 * 
	 * @param componentPos
	 *            the component position
	 * 
	 * @return this
	 */
	<T extends Generic> T disableReferentialIntegrity(Cache cache,
			int componentPos);

	/**
	 * Returns true if the referential integrity is enabled for component
	 * position
	 * 
	 * @param componentPos
	 *            the component position
	 * @return true if the referential integrity is enabled
	 */
	boolean isReferentialIntegrity(Context context, int componentPos);

	/**
	 * Returns the value of the attribute
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the value
	 */
	<S extends Serializable> S getValue(Context context, Attribute attribute);

	/**
	 * Set the value of attribute
	 * 
	 * @param attribute
	 * @param value
	 * @return the value holder node
	 */
	<T extends Value> T setValue(Cache cache, Attribute attribute,
			Serializable value);

	<T extends Generic> T getImplicit();

	<T extends Generic> Snapshot<T> getSupers();

	<T extends Generic> Snapshot<T> getComponents();

	int getComponentsSize();

	int getSupersSize();

	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation,
			Generic... targets);

	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation,
			int basePos, Generic... targets);

	<T extends Generic> Snapshot<T> getTargets(Context context,
			Relation relation);

	<T extends Generic> Snapshot<T> getTargets(Context context,
			Relation relation, int basePos, int targetPos);

	<T extends Value> Snapshot<T> getValueHolders(Context context, T attribute);

	<T extends Serializable> Snapshot<T> getValues(final Context context,
			final Attribute attribute);

	void log();

	String info();

	<T extends Generic> T newAnonymousInstance(Cache cache,
			Generic... components);

	<T extends Generic> T newInstance(Cache cache, Serializable value,
			Generic... components);

	<T extends Generic> T getMeta();

	<T extends Generic> Snapshot<T> getInheritings(Context context);

	<T extends Generic> Snapshot<T> getComposites(Context context);

	boolean isStructural();

	boolean isConcrete();

	boolean isMeta();

	boolean isTree();

}
