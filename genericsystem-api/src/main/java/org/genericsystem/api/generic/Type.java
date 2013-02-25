package org.genericsystem.api.generic;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;

/**
 * A Type.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Type extends Generic {

	/**
	 * Find an attribute by value.
	 * 
	 * @param context
	 *            The reference context.
	 * @param value
	 *            The attribute value.
	 * @return The attribute or null if not found.
	 */
	<T extends Attribute> T getAttribute(Context context, Serializable value);

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
	 * Create a property for the type.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            the property value
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T setProperty(Cache cache, Serializable value);

	/**
	 * Create a relation.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The relation value.
	 * @param targets
	 *            The targets of the relation.
	 * @return Return the relation.
	 * @see Relation
	 */
	<T extends Relation> T setRelation(Cache cache, Serializable value, Type... targets);

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
	 * @param basePos
	 *            The base position.
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(Cache cache, int basePos);

	/**
	 * Disable singular constraint for the base position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The base position.
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint(Cache cache, int basePos);

	/**
	 * Returns true if the singular constraint enabled for the base position
	 * 
	 * @param context
	 *            The reference context.
	 * @param basePos
	 *            The base position.
	 * @return true if the singular constraint enabled for the base position
	 */
	boolean isSingularConstraintEnabled(Context context, int basePos);

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

	/**
	 * Enable not null constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return Return this.
	 */
	<T extends Type> T enableNotNullConstraint(Cache cache);

	/**
	 * Disable not null constraint
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return this
	 */
	<T extends Type> T disableNotNullConstraint(Cache cache);

	/**
	 * Returns true if the not null constraint enabled
	 * 
	 * @param context
	 *            The reference context.
	 * @return true if the not null constraint enabled
	 */
	boolean isNotNullConstraintEnabled(Context context);

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
	 * @param basePos
	 *            The base position.
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint(Cache cache, int basePos);

	/**
	 * Disable required constraint for the base position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The base position.
	 * @return Return this.
	 */
	<T extends Type> T disableRequiredConstraint(Cache cache, int basePos);

	/**
	 * Returns true if the required constraint enabled for the base position
	 * 
	 * @param context
	 *            The reference context.
	 * @param basePos
	 *            The component position.
	 * @return true if the required constraint enabled for the base position
	 */
	boolean isRequiredConstraintEnabled(Context context, int basePos);

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
	 * Returns the type constraint imposed by the InstanceClass. By default is Object.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @return The type constraint imposed.
	 */
	Class<?> getConstraintClass(Cache cache);

	/**
	 * Modify the type constraint imposed by the InstanceClass.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param constraintClass
	 *            The type constraint imposed.
	 * @return Return this.
	 */
	<T extends Type> T setConstraintClass(Cache cache, Class<?> constraintClass);

	/**
	 * Returns the structure of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all attributes and relations of the Generic.
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getStructurals(Context context);

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
	 * Returns the relations of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all relations of the Generic.
	 * @see Snapshot
	 */
	<T extends Relation> Snapshot<T> getRelations(Context context);

	/**
	 * Returns the instances of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getInstances(Context context);

	/**
	 * Returns the instances of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all instances of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllInstances(Context context);

	/**
	 * Returns the sub types of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getSubTypes(Context context);

	/**
	 * Returns the sub types of Generic.
	 * 
	 * @param context
	 *            The reference context.
	 * @return The snapshot with all sub types of the Generic.
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getAllSubTypes(Context context);

}
