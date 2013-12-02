package org.genericsystem.generic;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;

/**
 * A Type.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Type extends Generic {

	/**
	 * Create a subtype. Throws an exception if already exists.
	 * 
	 * @param value
	 *            The type value.
	 * @param components
	 *            The components.
	 * @return Return the subtype.
	 */
	<T extends Type> T addSubType(Serializable value, Generic... components);

	/**
	 * Creates an attribute for the type. Throws an exception if already exists.
	 * 
	 * @param value
	 *            The attribute value.
	 * @return Return the attribute.
	 * @see Attribute
	 */
	<T extends Attribute> T addAttribute(Serializable value, Generic... targets);

	/**
	 * Creates a property for the type or throws an exception if this property already exists.
	 * 
	 * @param value
	 *            the property value
	 * @param targets
	 *            The target types.
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T addProperty(Serializable value, Generic... targets);

	/**
	 * Creates a relation or throws an exception if this relation already exists.
	 * 
	 * @param value
	 *            The relation value.
	 * @param targets
	 *            The target types.
	 * @return Return the relation.
	 * @see Relation
	 */
	<T extends Relation> T addRelation(Serializable value, Generic... targets);

	/**
	 * Create a new instance or throws an exception if this instance already exists.
	 * 
	 * @param value
	 *            the value.
	 * @param components
	 *            the components.
	 * 
	 * @return the new instance.
	 */
	<T extends Generic> T addInstance(Serializable value, Generic... components);

	/**
	 * Creates a new anonymous instance or throws an exception if this instance already exists.
	 * 
	 * @param components
	 *            the components.
	 * 
	 * @return the new anonymous instance.
	 */
	<T extends Generic> T addAnonymousInstance(Generic... components);

	/**
	 * Find a subtype by value.
	 * 
	 * @param value
	 *            The type value.
	 * @return The type or null if not found.
	 */
	<T extends Type> T getSubType(Serializable value);

	/**
	 * Find an attribute by value.
	 * 
	 * @param value
	 *            The attribute value.
	 * @param targets
	 *            The targets filter.
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(Serializable value, Generic... targets);

	/**
	 * Find the property by value.
	 * 
	 * @param value
	 *            The property value.
	 * @return Return the property or null if no property find.
	 */
	<T extends Attribute> T getProperty(Serializable value, Generic... targets);

	/**
	 * Find the relation by value.
	 * 
	 * @param value
	 *            The relation value.
	 * @param targets
	 *            TODO
	 * @return Return the relation or null if no relation find.
	 */
	<T extends Relation> T getRelation(Serializable value, Generic... targets);

	/**
	 * Returns the instance of Generic.
	 * 
	 * @param value
	 *            The value of the requested instance.
	 * @return The requested instance if it exists, null otherwise.
	 */
	<T extends Generic> T getInstance(Serializable value);

	/**
	 * Returns the direct sub types of Generic.
	 * 
	 * @return The snapshot with the direct sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getSubTypes();

	/**
	 * Returns the sub types of Generic and the sub types of the childrens.
	 * 
	 * @return The snapshot with all sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllSubTypes();

	/**
	 * Returns the attributes of Generic.
	 * 
	 * @return The snapshot with all attributes of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes();

	/**
	 * Returns the relations of Generic.
	 * 
	 * @return The snapshot with all relations of the Generic.
	 * @see Snapshot
	 */
	<T extends Relation> Snapshot<T> getRelations();

	/**
	 * Returns the instances of Generic.
	 * 
	 * @return The snapshot with instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInstances();

	/**
	 * Returns the instances of Generic and the instances of the childrens.
	 * 
	 * @return The snapshot with all instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllInstances();

	/**
	 * Returns the sub types with the given name.
	 * 
	 * @param name
	 *            - the name of sub types.
	 * @return Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllSubTypes(String name);

	/**
	 * Returns the sub type of Generic.
	 * 
	 * @param value
	 *            The sub type name.
	 * @return The sub type, or null if it does not exist.
	 */
	<T extends Generic> T getAllSubType(Serializable value);

	/**
	 * Returns the attributes of Generic.
	 * 
	 * @param attribute
	 *            The super attribute
	 * @return The snapshot with all attributes of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes(Attribute attribute);

	/**
	 * Create a subtype or returns this type if already exists.
	 * 
	 * @param value
	 *            The type value.
	 * @param components
	 *            The components.
	 * @return Return the subtype.
	 */
	<T extends Type> T setSubType(Serializable value, Generic... components);

	/**
	 * Creates an attribute for the type or returns this attribute if already exists.
	 * 
	 * @param value
	 *            The attribute value.
	 * @param targets
	 *            The targets
	 * @return Return the attribute.
	 * @see Attribute
	 */
	<T extends Attribute> T setAttribute(Serializable value, Generic... targets);

	/**
	 * Creates a property for the type or returns this property if already exists.
	 * 
	 * @param value
	 *            the property value
	 * @param targets
	 *            The target types.
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T setProperty(Serializable value, Generic... targets);

	/**
	 * Creates a relation or returns this relation if this relation already exists.
	 * 
	 * @param value
	 *            The relation value.
	 * @param targets
	 *            The target types.
	 * @return Return the relation.
	 * @see Relation
	 */
	<T extends Relation> T setRelation(Serializable value, Generic... targets);

	/**
	 * Create a new instance or get the instance if it already exists.
	 * 
	 * @param value
	 *            the value.
	 * @param components
	 *            the components.
	 * 
	 * @return the new instance.
	 */
	<T extends Generic> T setInstance(Serializable value, Generic... components);

	/**
	 * Create a new anonymous instance or get the instance if it already exists.
	 * 
	 * @param components
	 *            the components.
	 * 
	 * @return the new anonymous instance.
	 */
	<T extends Generic> T setAnonymousInstance(Generic... components);

	/**
	 * Enable singleton constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableSingletonConstraint();

	/**
	 * Disable singleton constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingletonConstraint();

	/**
	 * Returns true if the singleton constraint enabled
	 * 
	 * @return true if the singleton constraint enabled
	 */
	boolean isSingletonConstraintEnabled();

	/**
	 * Enable virtual constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableVirtualConstraint();

	/**
	 * Disable virtual constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T disableVirtualConstraint();

	/**
	 * Returns true if the virtual constraint enabled
	 * 
	 * @return true if the virtual constraint enabled
	 */
	boolean isVirtualConstraintEnabled();

	/**
	 * Returns the type constraint imposed by the InstanceClassConstraint. By default is Object.
	 * 
	 * @return The type constraint imposed.
	 */
	Class<?> getConstraintClass();

	/**
	 * Modify the type constraint imposed by the InstanceClassConstraint.
	 * 
	 * @param constraintClass
	 *            The type constraint imposed.
	 * @return Return this.
	 */
	<T extends Type> T setConstraintClass(Class<?> constraintClass);

}
