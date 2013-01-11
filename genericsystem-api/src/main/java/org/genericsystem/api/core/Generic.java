package org.genericsystem.api.core;

import java.io.Serializable;

import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Property;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;

/**
 * Generic is main interface of each node of the internal graph
 * 
 * @author Nicolas Feybesse
 * 
 */
public interface Generic extends Comparable<Generic> {

	/**
	 * Returns the root of the internal graph to which this generic belongs
	 * 
	 * @return Engine
	 */
	Engine getEngine();

	/**
	 * Returns true if this generic is the root of the internal graph
	 * 
	 * @return true if this generic is the root
	 */
	boolean isEngine();

	/**
	 * Returns true if this generic is an instance of the specified generic
	 * 
	 * @param generic
	 *            the type verify
	 * @return true if the Generic is a instance of
	 */
	boolean isInstanceOf(Generic generic);

	/**
	 * Returns instanciation level
	 * 
	 * @return instanciation level
	 */
	int getMetaLevel();

	/**
	 * Returns true if this generic is a Type
	 * 
	 * @return true if the Generic is a Type
	 */
	boolean isType();

	/**
	 * Returns true if this generic is an Attribute
	 * 
	 * @return true if the Generic is an Attribute
	 */
	boolean isAttribute();

	/**
	 * Returns true if this generic is an Really Attribute (no relation)
	 * 
	 * @return true if the Generic is an Really Attribute
	 */
	boolean isReallyAttribute();

	/**
	 * Returns true if this generic is an Attribute for the checked generic
	 * 
	 * @param generic
	 *            the checked generic
	 * 
	 * @return true if the Generic is an Attribute
	 */
	boolean isAttributeOf(Generic generic);

	/**
	 * Returns true if this generic is an Attribute for the checked generic and the component position
	 * 
	 * @param generic
	 *            the checked generic
	 * @param componentPos
	 *            the component position
	 * 
	 * @return true if the Generic is an Attribute
	 */
	boolean isAttributeOf(Generic generic, int componentPos);

	/**
	 * Returns true if this generic is an relation
	 * 
	 * @return true if the Generic is an Relation
	 */
	boolean isRelation();

	/**
	 * Returns the value of this generic
	 * 
	 * @return value
	 */
	<S extends Serializable> S getValue();

	/**
	 * Add a value for the attribute
	 * 
	 * @param attribute
	 *            the attribute
	 * @param value
	 *            the value
	 * @return the object that represents the value
	 * @see Value
	 */
	<T extends Value> T addValue(Cache cache, Value attribute, Serializable value);

	<T extends Value> T flag(Cache cache, Value attribute);

	<T extends Link> T addLink(Cache cache, Link relation, Serializable value, int basePos, Generic... targets);

	/**
	 * Add a link for the relation
	 * 
	 * @param relation
	 *            the relation
	 * @param value
	 *            the value of link
	 * @param generic
	 *            the generic links targets
	 * @return the link
	 * @see Link
	 */
	<T extends Link> T addLink(Cache cache, Link relation, Serializable value, Generic... generic);

	<T extends Link> T bind(Cache cache, Link relation, Generic... generic);

	<T extends Link> T bind(Cache cache, Link relation, int basePos, Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * @param targets
	 *            the optional targets
	 */
	<T extends Link> T getLink(Context context, Property property, Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * @param targets
	 *            the optional targets
	 */
	<T extends Link> T getLink(Context context, Property property, int basePos, Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * 
	 * @param value
	 *            the value
	 * @param targets
	 *            the optional targets
	 * @return
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value, Generic... targets);

	/**
	 * 
	 * @param property
	 *            the property
	 * 
	 * @param value
	 *            the value
	 * @param targets
	 *            the optional targets
	 * @return
	 */
	<T extends Link> T setLink(Cache cache, Link property, Serializable value, int basePos, Generic... targets);

	/**
	 * Returns true if the generic inherits from the given generic
	 * 
	 * @param generic
	 *            the checked generic
	 * @return true if the generic inherits from the given generic
	 */
	boolean inheritsFrom(Generic generic);

	/**
	 * Returns true if the generic inherits from all the given generics
	 * 
	 * @param generics
	 *            the given generics
	 * @return true if the generic inherits from all the given generics
	 */
	boolean inheritsFromAll(Generic... generics);

	/**
	 * Remove the generic
	 */
	void remove(Cache cache);

	/**
	 * Returns true if the generic is alive
	 * 
	 * @return true if the generic is alive
	 */
	boolean isAlive(Context context);

	/**
	 * Enable system property
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @return this
	 */
	<T extends Generic> T enableSystemProperty(Cache cache, Class<?> genericInCacheClass);

	/**
	 * Enable system property for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T enableSystemProperty(Cache cache, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Disable system property
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @return this
	 */
	<T extends Generic> T disableSystemProperty(Cache cache, Class<?> genericInCacheClass);

	/**
	 * Disable system property for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T disableSystemProperty(Cache cache, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Returns true if the system property is enabled
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @return true if the system property is enabled
	 */
	boolean isSystemPropertyEnabled(Context context, Class<?> genericInCacheClass);

	/**
	 * Returns true if the system property is enabled for component position
	 * 
	 * @param genericInCacheClass
	 *            classe which defines the generic in cache
	 * @param componentPos
	 *            the component position
	 * @return true if the system property is enabled
	 */
	boolean isSystemPropertyEnabled(Context context, Class<?> genericInCacheClass, int componentPos);

	/**
	 * Enable referential integrity for component position
	 * 
	 * @param componentPos
	 *            the component position
	 * @return this
	 */
	<T extends Generic> T enableReferentialIntegrity(Cache cache, int componentPos);

	/**
	 * Disable referential integrity for component position
	 * 
	 * @param componentPos
	 *            the component position
	 * 
	 * @return this
	 */
	<T extends Generic> T disableReferentialIntegrity(Cache cache, int componentPos);

	/**
	 * Returns true if the referential integrity is enabled for component position
	 * 
	 * @param componentPos
	 *            the component position
	 * @return true if the referential integrity is enabled
	 */
	boolean isReferentialIntegrity(Context context, int componentPos);

	/**
	 * Returns the value of property
	 * 
	 * @param property
	 *            the property
	 * @return the value
	 */
	<S extends Serializable> S getValue(Context context, Property property);

	/**
	 * Set the value of property
	 * 
	 * @param property
	 * @param value
	 * @return the value holder node
	 */
	<T extends Value> T setValue(Cache cache, Property property, Serializable value);

	<T extends Generic> T getImplicit();

	<T extends Generic> Snapshot<T> getSupers();

	<T extends Generic> Snapshot<T> getComponents();

	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation, Generic... targets);

	<T extends Link> Snapshot<T> getLinks(Context context, Relation relation, int basePos, Generic... targets);

	<T extends Generic> Snapshot<T> getTargets(Context context, Relation relation);

	<T extends Generic> Snapshot<T> getTargets(Context context, Relation relation, int basePos, int targetPos);

	<T extends Value> Snapshot<T> getValueHolders(Context context, T attribute);

	void log();

	String info();

	<T extends Generic> T newAnonymousInstance(Cache cache, Generic... components);

	<T extends Generic> T newInstance(Cache cache, Serializable value, Generic... components);

	<T extends Generic> T getMeta();

	<T extends Generic> Snapshot<T> getInheritings(Context context);

	<T extends Generic> Snapshot<T> getComposites(Context context);

	boolean isStructural();

	boolean isConcrete();

	boolean isMeta();

	boolean isTree();

	void cancel(Cache cache, Value attribute);

	void restore(Cache cache, Attribute attribute);

}
