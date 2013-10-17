package org.genericsystem.generic;

import org.genericsystem.core.Generic;

/**
 * An Attribute.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Attribute extends Holder, Type {

	/**
	 * Enable Size Constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * @param size
	 *            The size.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T enableSizeConstraint(int basePos, Integer size);

	/**
	 * Disable Size Constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T disableSizeConstraint(int basePos);

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

}
