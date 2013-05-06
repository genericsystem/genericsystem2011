package org.genericsystem.generic;

import java.io.Serializable;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
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
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all attributes of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes(Context context);

	/**
	 * Returns the attributes of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @param attribute
	 *            The super attribute
	 * @return The snapshot with all attributes of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes(final Context context, final Attribute attribute);

	/**
	 * Find an attribute by value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The attribute value. * @param targets The targets.
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(Context context, Serializable value, Generic... targets);

	/**
	 * Find an attribute by value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param attribute
	 *            The super attribute.
	 * @param value
	 *            The attribute value.
	 * @param targets
	 *            The targets
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(final Context context, Attribute attribute, final Serializable value, Generic... targets);

	/**
	 * Create an attribute for the type.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The attribute value.
	 * @return Return the attribute.
	 * @see Attribute
	 */
	<T extends Attribute> T setAttribute(Cache cache, Serializable value);

	/**
	 * Find the property by value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The property value.
	 * @return Return the property or null if no property find.
	 */
	<T extends Attribute> T getProperty(Context context, Serializable value);

	/**
	 * Create a property for the type.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            the property value
	 * @param targets
	 *            The target types.
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T setProperty(Cache cache, Serializable value, Type... targets);

	/**
	 * Returns the relations of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all relations of the Generic.
	 * @see Snapshot
	 */
	<T extends Relation> Snapshot<T> getRelations(Context context);

	/**
	 * Find the relation by value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The relation value.
	 * @return Return the relation or null if no relation find.
	 */
	<T extends Relation> T getRelation(Context context, Serializable value);

	/**
	 * Create a relation.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The relation value.
	 * @param targets
	 *            The target types.
	 * @return Return the relation.
	 * @see Relation
	 */
	<T extends Relation> T setRelation(Cache cache, Serializable value, Type... targets);

	/**
	 * Returns the instances of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInstances(Context context);

	/**
	 * Returns the instance of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The value of the requested instance.
	 * @return The requested instance if it exists, null otherwise.
	 */
	<T extends Generic> T getInstanceByValue(Context context, Serializable value);

	/**
	 * Returns the instances of Generic and the instances of the childrens.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllInstances(Context context);

	/**
	 * Returns the sub type of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The sub type name.
	 * @return The sub type, or null if it does not exist.
	 */
	<T extends Generic> T getSubType(Context context, Serializable value);

	/**
	 * Returns the direct sub types of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with the direct sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getDirectSubTypes(Context context);

	/**
	 * Returns the sub types of Generic and the sub types of the childrens.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getSubTypes(Context context);

	/**
	 * Create a subtype.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The type value.
	 * @param components
	 *            The components.
	 * @return Return the subtype.
	 */
	<T extends Type> T newSubType(Cache cache, Serializable value, Generic... components);

	/**
	 * Enable singular constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableSingularConstraint(Cache cache);

	/**
	 * Disable singular constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint(Cache cache);

	/**
	 * Returns true if the singular constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the singular constraint enabled
	 */
	boolean isSingularConstraintEnabled(Context context);

	/**
	 * Enable singular constraint for the base position
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(Cache cache, int componentPos);

	/**
	 * Disable singular constraint for the base position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint(Cache cache, int componentPos);

	/**
	 * Returns true if the singular constraint enabled for the base position
	 * 
	 * @param context
	 *            The reference context.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the singular constraint enabled for the base position
	 */
	boolean isSingularConstraintEnabled(Context context, int componentPos);

	/**
	 * Enable property constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this
	 */
	<T extends Type> T enablePropertyConstraint(Cache cache);

	/**
	 * Disable property constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this
	 */
	<T extends Type> T disablePropertyConstraint(Cache cache);

	/**
	 * Returns true if the property constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the property constraint enabled
	 */
	boolean isPropertyConstraintEnabled(Context context);

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
	 * Enable singular instance constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableSingularInstanceConstraint(Cache cache);

	/**
	 * Disable singular instance constraint
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return this
	 */
	<T extends Type> T disableSingularInstanceConstraint(Cache cache);

	/**
	 * Returns true if the singular instance constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the singular instance constraint enabled
	 */
	boolean isSingularInstanceConstraintEnabled(Context context);

	/**
	 * Enable required constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint(Cache cache);

	/**
	 * Disable required constraint
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint(Cache cache);

	/**
	 * Returns true if the required constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the required constraint enabled
	 */
	boolean isRequiredConstraintEnabled(Context context);

	/**
	 * Enable required constraint for the base position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint(Cache cache, int componentPos);

	/**
	 * Disable required constraint for the base position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T disableRequiredConstraint(Cache cache, int componentPos);

	/**
	 * Returns true if the required constraint enabled for the base position
	 * 
	 * @param context
	 *            The reference context.
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the required constraint enabled for the base position
	 */
	boolean isRequiredConstraintEnabled(Context context, int componentPos);

	/**
	 * Enable distinct constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableUniqueConstraint(Cache cache);

	/**
	 * Disable distinct constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T disableUniqueConstraint(Cache cache);

	/**
	 * Returns true if the distinct constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the distinct constraint enabled
	 */
	boolean isUniqueConstraintEnabled(Context context);

	/**
	 * Enable virtual constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableVirtualConstraint(Cache cache);

	/**
	 * Disable virtual constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T disableVirtualConstraint(Cache cache);

	/**
	 * Returns true if the virtual constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the virtual constraint enabled
	 */
	boolean isVirtualConstraintEnabled(Context context);

	/**
	 * Enable inheritance.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableInheritance(Cache cache);

	/**
	 * Disable inheritance.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T disableInheritance(Cache cache);

	/**
	 * Returns true if the inheritance enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the inheritance enabled
	 */
	boolean isInheritanceEnabled(Context context);

	/**
	 * Returns the type constraint imposed by the InstanceClassConstraint. By default is Object.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return The type constraint imposed.
	 */
	Class<?> getConstraintClass(Cache cache);

	/**
	 * Modify the type constraint imposed by the InstanceClassConstraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param constraintClass
	 *            The type constraint imposed.
	 * @return Return this.
	 */
	<T extends Type> T setConstraintClass(Cache cache, Class<?> constraintClass);

}
