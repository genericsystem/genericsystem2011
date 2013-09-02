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
	 * Returns the attributes of Generic.
	 * 
	 * @return The snapshot with all attributes of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes();

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
	 * Find an attribute by value.
	 * 
	 * @param value
	 *            The attribute value. * @param targets The targets.
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(Serializable value, Generic... targets);

	/**
	 * Find an attribute by value.
	 * 
	 * @param attribute
	 *            The super attribute.
	 * @param value
	 *            The attribute value.
	 * @param targets
	 *            The targets
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(Attribute attribute, Serializable value, Generic... targets);

	/**
	 * Creates an attribute for the type. Throws an exception if already exists.
	 * 
	 * @param value
	 *            The attribute value.
	 * @return Return the attribute.
	 * @see Attribute
	 */
	<T extends Attribute> T addAttribute(Serializable value);

	/**
	 * Creates an attribute for the type or returns this attribute if already exists.
	 * 
	 * @param value
	 *            The attribute value.
	 * @return Return the attribute.
	 * @see Attribute
	 */
	<T extends Attribute> T setAttribute(Serializable value);

	/**
	 * Find the property by value.
	 * 
	 * @param value
	 *            The property value.
	 * @return Return the property or null if no property find.
	 */
	<T extends Attribute> T getProperty(Serializable value);

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
	<T extends Attribute> T addProperty(Serializable value, Type... targets);

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
	<T extends Attribute> T setProperty(Serializable value, Type... targets);

	/**
	 * Returns the relations of Generic.
	 * 
	 * @return The snapshot with all relations of the Generic.
	 * @see Snapshot
	 */
	<T extends Relation> Snapshot<T> getRelations();

	/**
	 * Find the relation by value.
	 * 
	 * @param value
	 *            The relation value.
	 * @return Return the relation or null if no relation find.
	 */
	<T extends Relation> T getRelation(Serializable value);

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
	<T extends Relation> T addRelation(Serializable value, Type... targets);

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
	<T extends Relation> T setRelation(Serializable value, Type... targets);

	/**
	 * Returns the instances of Generic.
	 * 
	 * @return The snapshot with instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInstances();

	/**
	 * Returns the instance of Generic.
	 * 
	 * @param value
	 *            The value of the requested instance.
	 * @return The requested instance if it exists, null otherwise.
	 */
	<T extends Generic> T getInstance(Serializable value);

	/**
	 * Returns the instances of Generic and the instances of the childrens.
	 * 
	 * @return The snapshot with all instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllInstances();

	/**
	 * Returns the sub type of Generic.
	 * 
	 * @param value
	 *            The sub type name.
	 * @return The sub type, or null if it does not exist.
	 */
	<T extends Generic> T getSubType(Serializable value);

	/**
	 * Returns the direct sub types of Generic.
	 * 
	 * @return The snapshot with the direct sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getDirectSubTypes();

	/**
	 * Returns the sub types of Generic and the sub types of the childrens.
	 * 
	 * @return The snapshot with all sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getSubTypes();

	/**
	 * Create a subtype.
	 * 
	 * @param value
	 *            The type value.
	 * @param components
	 *            The components.
	 * @return Return the subtype.
	 */
	<T extends Type> T newSubType(Serializable value, Generic... components);

	/**
	 * Enable singular constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableSingularConstraint();

	/**
	 * Disable singular constraint.
	 * 
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint();

	/**
	 * Returns true if the singular constraint enabled
	 * 
	 * @return true if the singular constraint enabled
	 */
	boolean isSingularConstraintEnabled();

	/**
	 * Enable singular constraint for the base position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(int componentPos);

	/**
	 * Disable singular constraint for the base position.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint(int componentPos);

	/**
	 * Returns true if the singular constraint enabled for the base position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the singular constraint enabled for the base position
	 */
	boolean isSingularConstraintEnabled(int componentPos);

	/**
	 * Enable property constraint.
	 * 
	 * @return Return this
	 */
	<T extends Type> T enablePropertyConstraint();

	/**
	 * Disable property constraint.
	 * 
	 * @return Return this
	 */
	<T extends Type> T disablePropertyConstraint();

	/**
	 * Returns true if the property constraint enabled
	 * 
	 * @return true if the property constraint enabled
	 */
	boolean isPropertyConstraintEnabled();

	// /**
	// * Enable not null constraint.
	// *
	// * @param cache
	// * The reference Cache.
	// * @return Return this.
	// */
	// <T extends Type> T enableNotNullConstraint(Cache cache);
	//
	// /**
	// * Disable not null constraint
	// *
	// * @param cache
	// * The reference Cache.
	// * @return this
	// */
	// <T extends Type> T disableNotNullConstraint(Cache cache);
	//
	// /**
	// * Returns true if the not null constraint enabled
	// *
	// * @param context
	// * The reference context.
	// * @return true if the not null constraint enabled
	// */
	// boolean isNotNullConstraintEnabled(Context context);

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
	 * Enable required constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint();

	/**
	 * Disable required constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint();

	/**
	 * Returns true if the required constraint enabled
	 * 
	 * @return true if the required constraint enabled
	 */
	boolean isRequiredConstraintEnabled();

	/**
	 * Enable required constraint for the base position.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint(int componentPos);

	/**
	 * Disable required constraint for the base position.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T disableRequiredConstraint(int componentPos);

	/**
	 * Returns true if the required constraint enabled for the base position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the required constraint enabled for the base position
	 */
	boolean isRequiredConstraintEnabled(int componentPos);

	/**
	 * Enable unique value constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableUniqueValueConstraint();

	/**
	 * Disable unique value constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Type> T disableUniqueValueConstraint();

	/**
	 * Returns true if the unique value constraint enabled.
	 * 
	 * @return true if the unique value constraint enabled.
	 */
	boolean isUniqueValueConstraintEnabled();

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
