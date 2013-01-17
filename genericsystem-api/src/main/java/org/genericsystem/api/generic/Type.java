package org.genericsystem.api.generic;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;

/**
 * A Type
 * 
 * @author Nicolas Feybesse
 */
public interface Type extends Generic {

	/**
	 * Find an attribute by value
	 * 
	 * @param value
	 *            the attribute value
	 * @return the attribute or null if not found
	 */
	<T extends Attribute> T getAttribute(Context context, Serializable value);

	/**
	 * Find the property by value
	 * 
	 * @param value
	 *            the property value
	 * @return the property or null if no property find
	 */
	<T extends Attribute> T getProperty(Context context, Serializable value);

	/**
	 * Find the relation by value
	 * 
	 * @param value
	 *            the relation value
	 * @return the relation or null if no relation find
	 */
	<T extends Relation> T getRelation(Context context, Serializable value);

	/**
	 * Create a subtype
	 * 
	 * @param value
	 *            the type value
	 * @return the subtype
	 */
	<T extends Type> T newSubType(Cache cache, Serializable value, Generic... components);

	/**
	 * Create an attribute for the type
	 * 
	 * @param value
	 *            the attribute value
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T addAttribute(Cache cache, Serializable value);

	/**
	 * Create a sub attribute
	 * 
	 * @param attribute
	 *            the super attribute
	 * @param value
	 *            the sub attribute value
	 * @return the attribute
	 * @see Attribute
	 */
	// <T extends Attribute> T addSubAttribute(Cache cache, Attribute attribute, Serializable value);

	/**
	 * Create a property for the type <br/>
	 * 
	 * @param value
	 *            the property value
	 * @return the attribute
	 * @see Attribute
	 */
	<T extends Attribute> T addProperty(Cache cache, Serializable value);

	/**
	 * Create a property
	 * 
	 * @param value
	 *            the property value
	 * @return the property
	 * @see Property
	 */
	// <T extends Property> T addSubProperty(Cache cache, Property property, Serializable value);

	/**
	 * Create a relation
	 * 
	 * @param value
	 *            the relation value
	 * @param targets
	 *            the targets of the relation
	 * @return the relation
	 * @see Relation
	 */
	<T extends Relation> T addRelation(Cache cache, Serializable value, Type... targets);

	/**
	 * Create a relation
	 * 
	 * @param relation
	 *            the super relation
	 * @param value
	 *            the sub relation value
	 * @param targets
	 *            the targets of the relation
	 * @return the relation
	 * @see Relation
	 */
	// <T extends Relation> T addSubRelation(Cache cache, Relation relation, Serializable value, Type... targets);

	/**
	 * Enable singular constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(Cache cache);

	/**
	 * Disable singular constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingularConstraint(Cache cache);

	/**
	 * Returns true if the singular constraint enabled
	 * 
	 * @return true if the singular constraint enabled
	 */
	boolean isSingularConstraintEnabled(Context context);

	/**
	 * Enable singular constraint for the component position
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(Cache cache, int componentPos);

	/**
	 * Disable singular constraint for the component position
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingularConstraint(Cache cache, int componentPos);

	/**
	 * Returns true if the singular constraint enabled for the component position
	 * 
	 * @return true if the singular constraint enabled for the component position
	 */
	boolean isSingularConstraintEnabled(Context context, int componentPos);

	/**
	 * Enable property constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enablePropertyConstraint(Cache cache);

	/**
	 * Disable property constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disablePropertyConstraint(Cache cache);

	/**
	 * Returns true if the property constraint enabled
	 * 
	 * @return true if the property constraint enabled
	 */
	boolean isPropertyConstraintEnabled(Context context);

	/**
	 * Enable not null constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableNotNullConstraint(Cache cache);

	/**
	 * Disable not null constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableNotNullConstraint(Cache cache);

	/**
	 * Returns true if the not null constraint enabled
	 * 
	 * @return true if the not null constraint enabled
	 */
	boolean isNotNullConstraintEnabled(Context context);

	/**
	 * Enable singular instance constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingularInstanceConstraint(Cache cache);

	/**
	 * Disable singular instance constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingularInstanceConstraint(Cache cache);

	/**
	 * Returns true if the singular instance constraint enabled
	 * 
	 * @return true if the singular instance constraint enabled
	 */
	boolean isSingularInstanceConstraintEnabled(Context context);

	/**
	 * Enable required constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableRequiredConstraint(Cache cache);

	/**
	 * Disable required constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint(Cache cache);

	/**
	 * Returns true if the required constraint enabled
	 * 
	 * @return true if the required constraint enabled
	 */
	boolean isRequiredConstraintEnabled(Context context);

	/**
	 * Enable required constraint for the component position
	 * 
	 * @return this
	 */
	<T extends Type> T enableRequiredConstraint(Cache cache, int componentPos);

	/**
	 * Disable required constraint for the component position
	 * 
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint(Cache cache, int componentPos);

	/**
	 * Returns true if the required constraint enabled for the component position
	 * 
	 * @return true if the required constraint enabled for the component position
	 */
	boolean isRequiredConstraintEnabled(Context context, int componentPos);

	/**
	 * Enable distinct constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableUniqueConstraint(Cache cache);

	/**
	 * Disable distinct constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableUniqueConstraint(Cache cache);

	/**
	 * Returns true if the distinct constraint enabled
	 * 
	 * @return true if the distinct constraint enabled
	 */
	boolean isUniqueConstraintEnabled(Context context);

	/**
	 * Enable virtual constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableVirtualConstraint(Cache cache);

	/**
	 * Disable virtual constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableVirtualConstraint(Cache cache);

	/**
	 * Returns true if the virtual constraint enabled
	 * 
	 * @return true if the virtual constraint enabled
	 */
	boolean isVirtualConstraintEnabled(Context context);

	/**
	 * Enable inheritance
	 * 
	 * @return this
	 */
	<T extends Type> T enableInheritance(Cache cache);

	/**
	 * Disable inheritance
	 * 
	 * @return this
	 */
	<T extends Type> T disableInheritance(Cache cache);

	/**
	 * Returns true if the inheritance enabled
	 * 
	 * @return true if the inheritance enabled
	 */
	boolean isInheritanceEnabled(Context context);

	Class<? extends Serializable> getConstraintClass(Context context);

	void setConstraintClass(Cache cache, Class<?> constraintClass);

	<T extends Attribute> Snapshot<T> getStructurals(Context context);

	<T extends Attribute> Snapshot<T> getAttributes(Context context);

	<T extends Relation> Snapshot<T> getRelations(Context context);

	<T extends Generic> Snapshot<T> getInstances(Context context);

	<T extends Generic> Snapshot<T> getAllInstances(Context context);

	<T extends Generic> Snapshot<T> getSubTypes(Context context);

	<T extends Generic> Snapshot<T> getAllSubTypes(Context context);

}
